package com.mips.domain.order.enums;

public enum OrderStatus {
    PENDING,    // 검증 대기
    VALIDATED,  // 검증 완료
    REJECTED,   // 검증 실패 (잔고 부족 등)
    EXECUTED,   // 체결 완료
    SETTLED     // 정산 완료
}