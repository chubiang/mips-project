package com.mips.domain.comm.service;

import com.mips.domain.comm.enums.Currency;

import java.math.BigDecimal;

public interface ExchangeRateProvider {

    BigDecimal getRate(Currency from, Currency to);
}
