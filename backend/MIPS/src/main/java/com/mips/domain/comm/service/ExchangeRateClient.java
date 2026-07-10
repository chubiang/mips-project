package com.mips.domain.comm.service;

import com.mips.domain.comm.enums.Currency;

import java.math.BigDecimal;

public interface ExchangeRateClient {
    BigDecimal fetchRate(Currency from, Currency to);
}
