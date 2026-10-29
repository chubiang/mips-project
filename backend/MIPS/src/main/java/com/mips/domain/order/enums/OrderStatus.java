package com.mips.domain.order.enums;

public enum OrderStatus {
    PENDING,          // 검증 대기
    VALIDATED,        // 검증 완료, 체결 대기
    PARTIALLY_FILLED, // 일부 체결, 잔여 수량 체결 대기
    REJECTED,         // 주문 거절
    CANCELLED,        // 미체결 잔여 수량 취소
    EXPIRED,          // 유효기간 만료로 잔여 주문 종료
    EXECUTED,         // 전량 체결 완료
    SETTLED           // 정산 완료
}