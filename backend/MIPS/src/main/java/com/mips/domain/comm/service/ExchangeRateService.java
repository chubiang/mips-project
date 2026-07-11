package com.mips.domain.comm.service;

import com.mips.domain.comm.enums.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ExchangeRateService implements ExchangeRateProvider  {

    private final KoreaExchageRateClient exchangeRateClient;

    @Override
    public BigDecimal getRate(Currency from, Currency to) {
        if (from == to) {
            return BigDecimal.ONE;
        }

        return exchangeRateClient.fetchRate(from, to);
    }
}
