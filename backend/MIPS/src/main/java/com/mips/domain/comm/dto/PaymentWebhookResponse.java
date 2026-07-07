package com.mips.domain.comm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentWebhookResponse(
        String code,
        String header,
        String body,
        Instant respondedAt
) {
}
