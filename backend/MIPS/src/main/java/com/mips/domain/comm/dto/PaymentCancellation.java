package com.mips.domain.comm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentCancellation(
        String id,
        /** PG사 결제 취소 내역 아이디 */
        String pgCancellationId,
        /** 취소 금액 */
        Long totalAmount,
        /** 취소 금액 중 면세 금액 */
        Long taxFreeAmount,
        /** 취소 금액 중 부가세액 */
        Long vatAmount,
        /** 적립형 포인트의 환불 금액 */
        Long easyPayDiscountAmount,
        /** 취소 사유 */
        String reason,
        /** 취소 시점 */
        String cancelledAt,
        /** 취소 요청 시점 */
        String requestedAt,
        /** 취소 요청 경로 */
        String trigger
) {
}
