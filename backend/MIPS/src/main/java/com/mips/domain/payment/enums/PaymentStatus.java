package com.mips.domain.payment.enums;

public enum PaymentStatus {
    READY,                             // 대기
    VIRTUAL_ACCOUNT_ISSUED,   //
    PAID,                                // 완료
    FAILED,                             // 실패
    PARTIAL_CANCELLED,
    CANCELLED,
    PENDING
}
