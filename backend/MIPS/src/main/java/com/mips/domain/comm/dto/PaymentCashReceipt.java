package com.mips.domain.comm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentCashReceipt(
        String type,
        String pgReceiptId,
        String issueNumber,
        Long totalAmount,
        Long taxFreeAmount,
        String currency,
        String url,
        String issuedAt
) {
}
