package com.mips.domain.comm.dto;

import com.mips.domain.comm.enums.Currency;
import com.mips.domain.comm.enums.ExchangeRateType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRate(
        Currency from,
        Currency to,
        BigDecimal rate,
        ExchangeRateType type,
        LocalDate baseDate,
        int unit
) {
}