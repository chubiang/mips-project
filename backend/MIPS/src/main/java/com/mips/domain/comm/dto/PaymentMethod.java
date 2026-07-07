package com.mips.domain.comm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentMethod(
        String type,
        String provider,
        EasyPayMethod easyPayMethod
) {
}
