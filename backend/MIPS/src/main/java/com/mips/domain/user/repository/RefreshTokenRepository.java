package com.mips.domain.user.repository;

import com.mips.domain.user.entity.RefreshToken;
import com.mips.domain.user.enums.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 토큰 문자열로 검증 장부 찾기
    Optional<RefreshToken> findByToken(String token);

    // 탈취 감지 시 해당 유저의 모든 활성 토큰을 묶어서 다운시키기 위한 조회
    List<RefreshToken> findByEmailAndStatus(String email, TokenStatus status);

    @Modifying(clearAutomatically = true)
    @Query(
            value = "UPDATE refresh_token SET status = :status, revoke_at = :revokeAt WHERE token = :token",
            nativeQuery = true
    )
    int updateStatusByToken(@Param("status") String status, @Param("revokeAt") String revokeAt, @Param("token") String token);

}
