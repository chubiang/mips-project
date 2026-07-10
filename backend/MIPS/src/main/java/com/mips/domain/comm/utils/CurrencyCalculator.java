package com.mips.domain.comm.utils;

import com.mips.domain.comm.enums.Currency;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CurrencyCalculator {

    public BigDecimal convert(
            BigDecimal amount,
            BigDecimal rate,
            Currency targetCurrency
    ) {
        if (amount == null || rate == null || targetCurrency == null) {
            throw new IllegalArgumentException("환율 계산 값이 비어있습니다.");
        }

        return amount.multiply(rate)
                .setScale(targetCurrency.getScale(), RoundingMode.HALF_UP);
    }

}