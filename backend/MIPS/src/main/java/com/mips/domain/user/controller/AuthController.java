package com.mips.domain.user.controller;

import com.mips.domain.comm.dto.ApiResponse;
import com.mips.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        try {
            // refresh_token 찾기
            String refreshToken = extractRefreshTokenFromCookie(request);

            Map<String, Object> accessTokenMap =  userService.createAccessToken(refreshToken);
            return ResponseEntity.ok(ApiResponse.success(accessTokenMap));
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/refresh-user")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        try {
            // refresh_token 찾기
            String refreshToken = extractRefreshTokenFromCookie(request);

            Map<String, Object> accessTokenMap =  userService.getUserInfo(refreshToken);
            return ResponseEntity.ok(ApiResponse.success(accessTokenMap));
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // 쿠키 추출용
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
