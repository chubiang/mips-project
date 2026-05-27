package com.mips.global.enums;

public enum Role {
    ROLE_USER,    // 일반 사용자 (조회만 가능)
    ROLE_PREMIUM, // 결제 사용자 (결제)
    ROLE_ADMIN    // 최고 관리자 (수정, 삭제, 모든 권한)
}