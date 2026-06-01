package com.mips.domain.user.controller;

import com.mips.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/pass")
    public ResponseEntity<Map<String, Object>> getJwtToken() {
        // 1. 시큐리티 컨텍스트에서 현재 인증된 사용자 정보 꺼내기
        // (JwtAuthenticationFilter가 정상 작동했다면 여기에 정보가 들어있습니다)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. 필터에서 Principal 자리에 email을 넣었으므로, email을 꺼냅니다.
        String email = (String) authentication.getPrincipal();

        // 권한(Role) 정보 꺼내기
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // 3. JSON 형태로 응답할 데이터 조립
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "JWT 인증 성공!");
        response.put("email", email);
        response.put("role", role);

        return ResponseEntity.ok(response);
    }
}