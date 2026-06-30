package com.mips.domain.payment.dto;

import com.mips.domain.payment.entity.Payment;
import com.mips.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class PaymentResponse {
    private final User user;
    private final String storeId;
    private final String payMethod;
    private final String channel;
    private final String channelGroup;
    private final String version;
    private final String billingKey;

    private final String paymentId;
    private final String transactionId;
    private final String billNo;
    private final Integer amount;
    private final String status;
    private final LocalDateTime requestedAt;
    private final LocalDateTime statusChangedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Setter
    private String type;
    @Setter
    private String message;

    public PaymentResponse(Payment payment) {
        this.user = payment.getUser();
        this.transactionId = payment.getTransactionId();
        this.storeId = payment.getStoreId();
        this.paymentId = payment.getPaymentId();
        this.billNo = payment.getBillNo();
        this.amount = payment.getAmount();
        this.payMethod = payment.getPayMethod();
        this.channel = payment.getChannel();
        this.channelGroup = payment.getChannelGroup();
        this.version = payment.getVersion();
        this.billingKey = payment.getBillingKey();
        this.status = payment.getStatus().toString();
        this.requestedAt = payment.getRequestedAt();
        this.statusChangedAt = payment.getStatusChangedAt();
        this.createdAt = payment.getCreatedAt();
        this.updatedAt = payment.getUpdatedAt();
    }

}
