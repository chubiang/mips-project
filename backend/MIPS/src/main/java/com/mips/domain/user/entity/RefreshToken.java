package com.mips.domain.user.entity;

import com.mips.domain.user.enums.TokenStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "refresh_token", schema = "finance",
       indexes = {
        @Index(name = "idx_email_status", columnList = "email, status"),
        @Index(name = "idx_token_string", columnList = "token")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    /*
     TODO: 스프링 부트의 @Scheduled 기능을 이용해 "발급된 지 14일이 지난
      TokenStatus != ACTIVE인 데이터는
      매일 새벽마다 자동 삭제(Delete)" 해주는 스케줄러 작성하기
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenStatus status = TokenStatus.ACTIVE; // 기본값은 ACTIVE

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt; // 토큰 만료 예정 시간 (14일 뒤)

    @Column
    private LocalDateTime revokeAt;


    @Builder
    public RefreshToken(String email, String token, LocalDateTime expiresAt) {
        this.email = email;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    // 토큰 사용 완료 처리 (RTR 플로우용)
    public void changeStatusToUsed() {
        if (this.status != TokenStatus.ACTIVE) {
            throw new IllegalStateException("현재 활성화된 토큰만 사용 완료 상태로 변경할 수 있습니다.");
        }
        this.status = TokenStatus.USED;
    }

    // 강제 폐기 처리 (로그아웃 및 탈취 감지용)
    public void revoke() {
        this.status = TokenStatus.REVOKED;
    }

}
