package com.mips.domain.stock.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record RealtimeQuoteCache(
        String ticker,
        BigDecimal currentPrice,
        Instant quotedAt
) {
}
