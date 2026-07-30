package com.mips.domain.comm.service;

import com.mips.domain.comm.MoneyConversionResult;
import com.mips.domain.comm.dto.ExchangeRate;
import com.mips.domain.comm.enums.Currency;
import com.mips.domain.comm.enums.ExchangeRateType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CurrencyConversionService {
    private final ExchangeRateClient exchangeRateClient;

    public MoneyConversionResult convert(
            BigDecimal amount,
            Currency from,
            Currency to,
            ExchangeRateType rateType
    ) {
        ExchangeRate exchangeRate =
                exchangeRateClient.fetchRate(from, to, rateType);

        BigDecimal convertedAmount =
                calculate(amount, exchangeRate);

        return new MoneyConversionResult(
                amount,
                from,
                convertedAmount,
                to,
                exchangeRate.rate(),
                exchangeRate.baseDate()
        );
    }

    private BigDecimal calculate(BigDecimal amount, ExchangeRate exchangeRate) {
        return exchangeRate.rate().multiply(amount);
    }
}
