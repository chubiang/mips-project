package com.mips.domain.comm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentWebhookRequest(
        String header,
        String body,
        Instant requestedAt
) {
}
