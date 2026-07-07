package com.mips.domain.comm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.portone.sdk.server.payment.PaymentOriginPlatformType;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentOrigin(
        String platformType,
        String userAgent,
        String url,
        String ipAddress
) {
}