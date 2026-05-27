package com.mips.domain.comm.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private String userId; // 유저 아이디를 PK로 씁니다! (한 유저당 1개의 기기 로그인만 허용하는 구조)

    private String token; // 발급된 Refresh Token 값

    public RefreshToken(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    // 토큰 갱신할 때 쓰는 메서드
    public void updateToken(String token) {
        this.token = token;
    }
}