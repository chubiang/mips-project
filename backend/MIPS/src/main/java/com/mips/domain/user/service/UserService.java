package com.mips.domain.user.service;

import com.mips.domain.user.dto.SignupRequuest;
import com.mips.domain.user.dto.SignupResponse;
import com.mips.domain.user.entity.RefreshToken;
import com.mips.domain.user.entity.User;
import com.mips.domain.user.enums.TokenStatus;
import com.mips.domain.user.repository.RefreshTokenRepository;
import com.mips.domain.user.repository.UserRepository;
import com.mips.global.component.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtProvider jwtProvider;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    /*
      토큰 생성하기
     */
    @Transactional
    public Map<String, Object> createAccessToken(@CookieValue("refreshToken") String refreshToken) throws NoSuchAlgorithmException, InvalidKeyException {
        // 1. DB에 저장된 토큰 검사하기
        User userInfo = getUserByRefreshToken(refreshToken);

        // 2. 새로운 Access Token 발급!
        String email = userInfo.getEmail();
        String role = userInfo.getRole().toString();
        String newAccessToken = jwtProvider.createAccessToken(email, role);
        log.info("newAccessToken {}", newAccessToken);
        return Map.of("token", newAccessToken);
    }

    /*
       리프레시 토큰으로 회원 기본정보 가져오기
     */
    @Transactional
    public Map<String, Object> getUserInfo(String refreshToken) {
        // 1. DB에 저장된 토큰 검사하기
        User userInfo = getUserByRefreshToken(refreshToken);

        // 2. 회원 기본정보 가져오기
        String email = userInfo.getEmail();
        String role = userInfo.getRole().toString();

        return Map.of("email", email, "role", role, "nickname", userInfo.getUsername());
    }

    /*
       리프레시 토큰으로 회원 기본정보 조회하기
     */
    public User getUserByRefreshToken(String refreshToken) {
        // 1. DB에 저장된 토큰 검사하기
        RefreshToken tokenDB = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않거나 만료된 리프레시 토큰입니다."));

        if (tokenDB.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.updateStatusByToken(TokenStatus.REVOKED.name(), LocalDateTime.now(), refreshToken);
            if (tokenDB.getStatus() != TokenStatus.ACTIVE) {
                throw new IllegalArgumentException("유효하지 않거나 만료된 리프레시 토큰입니다.");
            }
        }
        // 2. 맞는 리프레시 토큰이면 DB에 조회해서 role 가져오기
        User user = userRepository.findByEmail(tokenDB.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("회원정보를 찾을 수 없습니다."));

        log.info("getUser = Email {} / Role {}", user.getEmail(), user.getRole());

        return user;
    }

    /*
      토큰 만료체크
     */
    public boolean isTokenExpired(RefreshToken refreshToken) {
        // 현재 시각이 만료 예정 시각(expiresAt)을 지났다면 true (만료됨)
        return LocalDateTime.now().isAfter(refreshToken.getExpiresAt());
    }
}
