package com.mips.domain.comm.service;

import com.mips.domain.comm.enums.Currency;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class KoreaExchageRateClient implements ExchangeRateClient {

    @Override
    public BigDecimal fetchRate(Currency from, Currency to) {
        // 1. 외부 API 호출
        // 2. USD/KRW, JPY/KRW 환율 응답 파싱
        // 3. 필요한 BigDecimal 환율 반환

        throw new UnsupportedOperationException("외부 환율 API 연동 예정");
    }
}
