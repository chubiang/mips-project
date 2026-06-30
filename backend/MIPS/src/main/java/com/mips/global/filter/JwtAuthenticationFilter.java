package com.mips.global.filter;

import com.mips.global.component.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // oauth2 로그인 시작 주소와 로그인 성공 코드를 보내는 콜백 주소는 JWT 검사를 하지 않습니다.
        return path.startsWith("/oauth2/authorization/") || path.startsWith("/login/oauth2/code/")
                || path.startsWith("/api/auth/refresh") || path.startsWith("/api/auth/refresh-user");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. HTTP 헤더에서 토큰 꺼내기
        String token = resolveToken(request);
        log.info("token: {}",token);
        // 2. 토큰이 존재하고, 리프레시 토큰이 아닌 경우, 유효한지 검사
        if (token != null && jwtProvider.validateToken(token) && !jwtProvider.isRefreshToken(token)) {
            // 3. 토큰에서 유저 정보(이메일, 권한) 추출
            String email = jwtProvider.getEmailFromToken(token);
            String role = jwtProvider.getRoleFromToken(token);
            log.info("email: {} / role: {}",email, role);

            // 4. Spring Security가 이해할 수 있는 인증 객체(Authentication) 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    email,
                    null, // 비밀번호는 없으므로 null
                    Collections.singleton(new SimpleGrantedAuthority(role))
            );

            // 5. 생성한 인증 객체를 SecurityContext에 저장 (이 요청이 끝날 때까지 "이 유저는 인증됨" 상태 유지)
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 6. 다음 필터로 요청 넘기기
        filterChain.doFilter(request, response);
    }

    // HTTP 요청 헤더에서 토큰만 깔끔하게 분리하는 유틸 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("bearerToken: {}",bearerToken);
        // 토큰은 "Bearer xxxxx..." 형태로
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 글자(7칸)를 잘라내고 실제 토큰만 반환
        }
        return null;
    }
}
