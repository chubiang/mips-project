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
        Optional<RefreshToken> tokenDB = refreshTokenRepository.findByToken(refreshToken);

        if (tokenDB.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 리프레시 토큰입니다.");
        }
        else if (tokenDB.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.updateStatusByToken(TokenStatus.REVOKED.name(), LocalDateTime.now(), refreshToken);
            if (tokenDB.get().getStatus() != TokenStatus.ACTIVE) {
                throw new IllegalArgumentException("유효하지 않거나 만료된 리프레시 토큰입니다.");
            }
        }
        // 2. 맞는 리프레시 토큰이면 DB에 조회해서 role 가져오기
        Optional<User> userInfo = userRepository.findByEmail(tokenDB.get().getEmail());
        log.info("createAccessToken count {}", userInfo.stream().count());

        // 3. 새로운 Access Token 발급!
        String email = userInfo.orElseThrow().getEmail();
        String role = userInfo.orElseThrow().getRole().toString();
        String newAccessToken = jwtProvider.createAccessToken(email, role);
        log.info("newAccessToken {}", newAccessToken);
        return Map.of("token", newAccessToken);
    }

    public SignupResponse signup(SignupRequuest signup)
    {
        
        return null;
    }

    /*
      토큰 만료체크
     */
    public boolean isTokenExpired(RefreshToken refreshToken) {
        // 현재 시각이 만료 예정 시각(expiresAt)을 지났다면 true (만료됨)
        return LocalDateTime.now().isAfter(refreshToken.getExpiresAt());
    }

    @Transactional
    public Map<String, Object> getUserInfo(String refreshToken) {
        // 1. DB에 저장된 토큰 검사하기
        Optional<RefreshToken> tokenDB = refreshTokenRepository.findByToken(refreshToken);

        if (tokenDB.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 리프레시 토큰입니다.");
        }
        else if (tokenDB.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.updateStatusByToken(TokenStatus.REVOKED.name(), LocalDateTime.now(), refreshToken);
            if (tokenDB.get().getStatus() != TokenStatus.ACTIVE) {
                throw new IllegalArgumentException("유효하지 않거나 만료된 리프레시 토큰입니다.");
            }
        }
        // 2. 맞는 리프레시 토큰이면 DB에 조회해서 role 가져오기
        Optional<User> userInfo = userRepository.findByEmail(tokenDB.get().getEmail());
        log.info("getUserInfo count {}", userInfo.stream().count());

        // 3. 새로운 Access Token 발급!
        String email = userInfo.orElseThrow().getEmail();
        String role = userInfo.orElseThrow().getRole().toString();

        return Map.of("email", email, "role", role, "nickname", userInfo.orElseThrow().getUsername());
    }
}
