package com.mips.domain.user.service;

import com.mips.domain.user.dto.SignupRequuest;
import com.mips.domain.user.dto.SignupResponse;
import com.mips.domain.user.entity.RefreshToken;
import com.mips.domain.user.enums.TokenStatus;
import com.mips.domain.user.repository.RefreshTokenRepository;
import com.mips.global.component.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final JwtProvider jwtProvider;

    private final RefreshTokenRepository refreshTokenRepository;

    /*
      토큰 생성하기
     */
    public Map<String, Object> createAccessToken(String refreshToken) throws NoSuchAlgorithmException, InvalidKeyException {

        if (refreshToken == null || !jwtProvider.validateToken(refreshToken))
        {
            throw new IllegalArgumentException("유효하지 않거나 만료된 리프레시 토큰입니다.");
        }
        String hashToken = jwtProvider.hmacSha256(refreshToken);
        log.info("hashToken {}", hashToken);
        // DB에 저장된 토큰 검사하기
        Optional<RefreshToken> tokenDB = refreshTokenRepository.findByToken(hashToken);

        if (tokenDB.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 리프레시 토큰입니다.");
        }
        else if (tokenDB.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            if (tokenDB.get().getStatus() != TokenStatus.ACTIVE)
            {
                refreshTokenRepository.updateStatusByToken(TokenStatus.REVOKED.name(), LocalDateTime.now().toString(), refreshToken);
            }
            throw new IllegalArgumentException("유효하지 않거나 만료된 리프레시 토큰입니다.");
        }

        // 새로운 Access Token 발급!
        String email = jwtProvider.getEmailFromToken(refreshToken);
        String role = jwtProvider.getRoleFromToken(refreshToken);
        String newAccessToken = jwtProvider.createAccessToken(email, role);
        log.info("newAccessToken {}", newAccessToken);
        return Map.of("accessToken", newAccessToken);
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

}
