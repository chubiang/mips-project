package com.mips.domain.payment.dto;

import com.mips.domain.user.entity.User;
import io.portone.sdk.server.common.*;
import io.portone.sdk.server.common.SelectedChannel;
import io.portone.sdk.server.payment.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@Setter
@Getter
public class PortOneResponse {
    private final User user;
    private final String storeId;
    private final PaymentMethod payMethod;  // payMethod
    private final SelectedChannel channel; // (결제, 본인인증 등에) 선택된 채널 정보
    private final ChannelGroupSummary channelGroup; // 채널 그룹 정보
    private final PortOneVersion version; // 포트원 버전
    private final String billingKey; // 결제 시 사용된 빌링키

    private final String id; // paymentId
    private final String transactionId; // 결제 건 포트원 채번 아이디
    private final String billNo; // 영수증번호
    private final PaymentAmount amount;
    private final Currency currency;
    private final Customer customer;
    private final PaymentOrigin origin;
    private final String status;
    private final List<PaymentWebhook> webhooks;
    private final LocalDateTime requestedAt;
    private final LocalDateTime statusChangedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Integer productCount;
    private final String customData;
    private final Country country;
    private final PaymentEscrow escrow;
    private final List<PaymentProduct> products;

    /* status에 따라 사용됨 */
    private final CancelledPayment cancelledPayment;   // CANCELLED
    private final FailedPayment failedPayment;         // FAILED
    private final PaidPayment paidPayment;             // PAID
    private final PartialCancelledPayment partialCancelledPayment; // PARTIAL_CANCELLED
    private final PayPendingPayment payPendingPayment; // PAY_PENDING
    private final ReadyPayment readyPayment;           // READY
    private final VirtualAccountIssuedPayment virtualAccountIssuedPayment; // VIRTUAL_ACCOUNT_ISSUED

    private String type;
    private String message;

}
