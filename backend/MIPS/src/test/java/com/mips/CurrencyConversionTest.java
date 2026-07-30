package com.mips;

import com.mips.domain.comm.enums.Currency;
import com.mips.domain.comm.enums.ExchangeRateType;
import com.mips.domain.comm.service.KoreaEximExchangeRateClient;
import com.mips.global.config.KoreaEximProperties;
import com.mips.global.config.PortOneSecretProperties;
import com.mips.global.config.RestClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestClient;

@SpringJUnitConfig
@Import({
        RestClientConfig.class,
        KoreaEximExchangeRateClient.class
})
@EnableConfigurationProperties({KoreaEximProperties.class, PortOneSecretProperties.class})
@TestPropertySource(properties = {
        "portone.secret.api=https://api.portone.io",
        "portone.secret.store-id=store-60b6cde7-1145-41de-8144-b820e7dc1335",
        "portone.secret.secret=${PORTONE-SECRET}",
        "koreaexim.exchange-url=https://oapi.koreaexim.go.kr",
        "koreaexim.secret=${KOREA_EXIM_AUTH_KEY}"
})
class CurrencyConversionTest {

    @Autowired
    @Qualifier("koreaEximRestClient")
    RestClient koreaEximRestClient;

    @Autowired
    KoreaEximExchangeRateClient koreaEximExchangeRateClient;

    @Test
    void conversionTest(){
        var result = koreaEximExchangeRateClient.fetchRate(Currency.KRW, Currency.USD, ExchangeRateType.DEAL_BASE);
        System.out.println(result);

    }

}
