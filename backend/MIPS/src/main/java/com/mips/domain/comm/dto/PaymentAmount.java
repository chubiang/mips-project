package com.mips.domain.comm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentAmount(
        Long total,
        Long taxFree,
        Long vat,
        Long supply,
        Long discount,
        Long paid,
        Long cancelled,
        Long cancelledTaxFree
) {
}