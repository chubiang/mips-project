package com.mips.domain.stock.dto;

public record NasdaqDataHeader(
    String symbol,
    String companyName,
    String marketCap,
    String lastSalePrice,
    String netChange,
    String percentageChange
) {
}
