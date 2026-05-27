package com.mips.domain.user.entity;

import com.mips.domain.comm.entity.BaseTimeEntity;
import com.mips.global.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user", schema = "finance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근을 막아 안전성 확보
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Builder.Default
    @Column(nullable = false, length = 14)
    private String phone = "000-0000-0000";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User(String username, String email, Role role, String phone) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.phone = phone;
    }
    // OAuth2 계정의 이름이 변경되었을 때 DB에 반영하기 위한 업데이트
    public User update(String username) {
        this.username = username;
        return this;
    }
}