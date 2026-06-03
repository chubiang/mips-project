package com.mips.global.config;

import com.mips.global.component.OAuth2SuccessHandler;
import com.mips.global.filter.JwtAuthenticationFilter;
import com.mips.global.service.CustomOAuth2UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Value("${app.oauth2.failed-redirect-uri}")
    private String failedRedirectUri;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {

        http
            // 1. CSRF 방어 기능 끄기 : 세션 미사용
            .csrf(AbstractHttpConfigurer::disable)
            // 2. CORS 설정 적용 (리액트 5173 포트 허용)
            //.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .cors(Customizer.withDefaults())
            // 3. 세션 관리 상태를 'STATELESS'로 설정 (서버가 세션을 기억하지 않음, 나중에 JWT 쓸 때 필수)
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 4. 요청 경로별 권한 설정 (antMatchers 대신 requestMatchers 사용)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/",
                                    "/login",
                                    "/oauth2/**",
                                    "/login/oauth2/code/google",
                                    "/login/oauth2/code/kakao",
                                    "/favicon.ico",
                                    "/error",
                                    "/api/login",
                                    "/api/signup",
                                    "/api/auth/refresh",
                                    "/api/stock/**").permitAll()
                    .requestMatchers("/api/user/**", "/api/order/**","/api/pay/**").authenticated()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")  // 관리자 전용
                    .anyRequest().authenticated() // 그 외 나머지 요청은 인증(로그인) 필요
            )
            // 5. OAuth2 로그인 설정
            .oauth2Login(oauth2 -> oauth2
                    .userInfoEndpoint(userInfo -> userInfo
                            .userService(customOAuth2UserService))
                    .successHandler(oAuth2SuccessHandler)
                    .failureHandler((request, response, exception) -> {
                        System.out.println("🚨 소셜 로그인 실패: " + exception.getMessage());
                        response.sendRedirect(failedRedirectUri);
                    })
            )
            // 6. 권한없는 경로 접근 시 예외처리
            .exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setContentType("application/json;charset=UTF-8");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"인증 토큰이 없거나 유효하지 않습니다.\"}");
                    })
            )
            // FINAL: 폼 로그인, Basic Http 로그인 비활성화 (리액트가 화면을 알아서 그리므로)
            .formLogin(form -> form.disable())
            .logout(logout -> logout
                    .logoutUrl("/api/auth/logout")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("refresh_token")
                    .logoutSuccessHandler((request, response, authentication) -> {
                        response.setStatus(HttpServletResponse.SC_OK);
                    })
            )
            .httpBasic(basic -> basic.disable());
            // ADD: JWT 필터를 추가
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 💡 리액트(5173)와의 통신을 위한 CORS 설정 빈
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // 리액트 주소
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
