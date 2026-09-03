package com.mips.domain.stock.dto;

public record NasdaqDataStatus(
        int rCode,
        String bCodeMessage,
        String developerMessage
) {
}
