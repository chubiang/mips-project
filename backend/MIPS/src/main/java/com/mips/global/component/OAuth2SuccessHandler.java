package com.mips.global.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.oauth2.authorized-redirect-uri}")
    private String redirectUri;

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("🚀🚀🚀 OAuth2SuccessHandler 진입 성공!!! 🚀🚀🚀");

        // 로그인 성공한 유저의 정보 꺼내기
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String role = oAuth2User.getRole();
        String token = oAuth2User.getAttribute("token");

        if (token == null) {
            token = jwtProvider.createAccessToken(email, role);
        }
        log.info("email = {}, role = {}, token = {}", email, role, token);
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .build().toUriString();

        if (response.isCommitted()) {
            System.out.println("🚨 응답이 이미 커밋되어 리다이렉트 할 수 없습니다.");
            return;
        }

        // 이 한 줄이 브라우저에게 "5173으로 가라!"고 지시합니다. (절대 super 호출 금지)
        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }
}
