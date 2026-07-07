package com.mips.domain.comm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentWebhook(
        String paymentStatus,
        String id,
        String status,
        String url,
        Boolean isAsync,
        Integer currentExecutionCount,
        Integer maxExecutionCount,
        String trigger,
        PaymentWebhookRequest request,
        PaymentWebhookResponse response,
        Instant triggeredAt
) {
}