package com.mips;

import com.mips.domain.account.repository.AccountBalanceRepository;
import com.mips.domain.charge.repository.ChargeRepository;
import com.mips.domain.charge.service.ChargeService;
import com.mips.domain.comm.enums.Currency;
import com.mips.domain.comm.service.ExchangeRateProvider;
import com.mips.domain.comm.utils.CurrencyCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchageRateTests {
    @Mock
    ExchangeRateProvider exchangeRateProvider;

    @Mock
    ChargeRepository chargeRepository;

    @Mock
    AccountBalanceRepository accBalanceRepository;

    CurrencyCalculator currencyCalculator = new CurrencyCalculator();

    @InjectMocks
    ChargeService chargeService;

    @Test
    void exchageUSDToKRW() {
        when(exchangeRateProvider.getRate(Currency.USD, Currency.KRW))
                .thenReturn(new BigDecimal("1380.50"));
    }
}
