package com.mips.domain.stock.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public record FinnhubTradeData(
        String s,
        BigDecimal p,
        Long t,
        Long v,
        List<String> c
) {
    public LocalDateTime getTimestamp() {
        Instant instant = Instant.ofEpochMilli(this.t);

        return LocalDateTime.ofInstant(
                instant,
                ZoneId.of("Asia/Seoul")
        );
    }
}
