package com.mips.domain.stock.dto;

public record Nasdaq100Response(
        NasdaqData data,
        String message,
        NasdaqDataStatus status
) {
}
