package com.mips.domain.comm.service;

import com.mips.domain.comm.dto.ExchangeRate;
import com.mips.domain.comm.enums.Currency;
import com.mips.domain.comm.enums.ExchangeRateType;

import java.math.BigDecimal;

public interface ExchangeRateClient {
    ExchangeRate fetchRate(Currency from, Currency to, ExchangeRateType type);
}
