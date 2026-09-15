package com.mips.domain.stock.dto;

import com.mips.domain.stock.enums.SecurityType;

import java.math.BigDecimal;
import java.time.Instant;

public record RealtimeStockResponse(
        String ticker,
        String companyName,
        SecurityType assetType,
        BigDecimal currentPrice,
        Instant quotedAt
) {
}
