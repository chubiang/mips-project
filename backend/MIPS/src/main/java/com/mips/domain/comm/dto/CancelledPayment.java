package com.mips.domain.comm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CancelledPayment(
        String id,
        String transactionId,
        String merchantId,
        String storeId,
        PaymentMethod method,
        SelectedChannel channel,
        ChannelGroupSummary channelGroup,
        String version,
        String scheduleId,
        String billingKey,
        List<PaymentWebhook> webhooks,
        Instant requestedAt,
        Instant updatedAt,
        Instant statusChangedAt,
        String orderName,
        PaymentAmount amount,
        String currency,
        Customer customer,
        PaymentOrigin origin,
        String promotionId,
        Boolean isCulturalExpense,

        Integer productCount,
        String customData,
        String country,
        Instant paidAt,
        String pgTxId,
        PaymentCashReceipt cashReceipt,
        String receiptUrl,
        List<PaymentCancellation> cancellations,
        Instant cancelledAt
) {
}