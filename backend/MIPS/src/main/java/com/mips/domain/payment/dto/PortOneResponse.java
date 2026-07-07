package com.mips.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mips.domain.comm.dto.*;
import com.mips.domain.user.entity.User;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PortOneResponse (
        User user,
        String storeId,
        PaymentMethod method,  // payMethod
        SelectedChannel channel, // (결제, 본인인증 등에) 선택된 채널 정보
        ChannelGroupSummary channelGroup, // 채널 그룹 정보
        String version, // 포트원 버전
        String billingKey, // 결제 시 사용된 빌링키

        String id, // paymentId
        String transactionId, // 결제 건 포트원 채번 아이디
        String billNo, // 영수증번호
        PaymentAmount amount,
        String currency,
        Customer customer,
        PaymentOrigin origin,
        String status,
        List<PaymentWebhook> webhooks,
        Instant requestedAt,
        Instant statusChangedAt,
        Instant createdAt,
        Instant updatedAt,
        Integer productCount,
        String customData,
        Instant country,
        Instant cancelledAt,
        Instant failedAt,
        Instant paidAt,
        String pgTxId,

         /* status에 따라 사용됨 - 미리보기용 */
//        CancelledPayment cancelledPayment,   // CANCELLED
//        FailedPayment failedPayment,         // FAILED
//        PaidPayment paidPayment,             // PAID
//        PartialCancelledPayment partialCancelledPayment, // PARTIAL_CANCELLED
//        PayPendingPayment payPendingPayment, // PAY_PENDING
//        ReadyPayment readyPayment,           // READY
//        VirtualAccountIssuedPayment virtualAccountIssuedPayment, // VIRTUAL_ACCOUNT_ISSUED

        String type,
        String message
)
{}
