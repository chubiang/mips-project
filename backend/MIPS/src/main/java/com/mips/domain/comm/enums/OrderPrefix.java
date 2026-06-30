package com.mips.domain.comm.enums;

import lombok.Getter;

/**
 * 식별자 생성을 위한 도메인별 접두어 Enum
 */
@Getter
public enum OrderPrefix {
    ORDER("ORD"),    // 상품 주문
    CHARGE("CHG"),   // 포인트 충전
    PAYMENT("PAY"),  // 결제 트랜잭션
    REFUND("REF");   // 환불

    private final String code;

    OrderPrefix(String code) {
        this.code = code;
    }

}
