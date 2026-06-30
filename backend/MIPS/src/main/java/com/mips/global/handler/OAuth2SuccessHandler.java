package com.mips.global.handler;

import com.mips.domain.user.entity.RefreshToken;
import com.mips.domain.user.enums.TokenStatus;
import com.mips.domain.user.repository.RefreshTokenRepository;
import com.mips.global.component.CustomOAuth2User;
import com.mips.global.component.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${cookie.same-site:Lax}")
    private String sameSite;

    @Value("${app.oauth2.authorized-redirect-uri}")
    private String redirectUri;

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("🚀🚀🚀 OAuth2SuccessHandler 진입 성공!!! 🚀🚀🚀");

        // 로그인 성공한 유저의 정보 꺼내기
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String role = oAuth2User.getRole();
        String token = oAuth2User.getAttribute("token");
        String refreshToken = ""; // 리프레시 토큰
        LocalDateTime expireTime = null; // 리프레시 토큰 만료되는 시간

        if (token == null) {
            token = jwtProvider.createAccessToken(email, role);
        }
        // 이전 발급받은 리프레시 토큰이 있는지 조회함
        List<RefreshToken> refreshTokenList = refreshTokenRepository.findByEmailAndStatus(email, TokenStatus.ACTIVE);
        if (refreshTokenList.isEmpty())
        {
            refreshToken = jwtProvider.createRefreshToken(email);
            expireTime = jwtProvider.getExpirationFromToken(refreshToken);

            try {
                String hashToken = jwtProvider.hmacSha256(refreshToken);
                log.info("hashToken {}", hashToken);
                // 토큰 저장
                refreshTokenRepository.save(new RefreshToken(email, hashToken, expireTime));
            } catch (InvalidKeyException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        else
        {
            refreshToken = refreshTokenList.get(0).getToken();
            expireTime =  refreshTokenList.get(0).getExpiresAt();
        }
        log.info("[OAuth2SuccessHandler] refreshToken: {} expireTime : {}",refreshToken, expireTime);

        // Refresh Token을 HttpOnly 쿠키로 굽기
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true) // JS에서 document.cookie로 접근 불가 (XSS 완벽 차단)
                .secure(cookieSecure)  // HTTPS 쓸 때는 true로 변경 (지금은 localhost라 false)
                .sameSite(sameSite)
                .path("/")
                .maxAge(Duration.ofDays(14)) // 14일 유지
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        log.info("email = {}, role = {}, token = {}", email, role, token);
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .build().toUriString();

        if (response.isCommitted()) {
            System.out.println("🚨 응답이 이미 커밋되어 리다이렉트 할 수 없습니다.");
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }
}
