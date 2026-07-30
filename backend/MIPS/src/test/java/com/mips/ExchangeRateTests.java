package com.mips;

import com.mips.domain.charge.repository.ChargeRepository;
import com.mips.domain.charge.service.ChargeService;
import com.mips.domain.comm.dto.ExchangeRate;
import com.mips.domain.comm.enums.Currency;
import com.mips.domain.comm.enums.ExchangeRateType;
import com.mips.domain.comm.service.KoreaEximExchangeRateClient;
import com.mips.domain.comm.utils.CurrencyCalculator;
import com.mips.global.config.KoreaEximProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateTests {

    private MockRestServiceServer mockServer;
    private KoreaEximExchangeRateClient client;


    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://oapi.koreaexim.go.kr");

        mockServer = MockRestServiceServer
                .bindTo(builder)
                .build();

        RestClient koreaEximRestClient = builder.build();
        KoreaEximProperties koreaEximProperties = new KoreaEximProperties();
        koreaEximProperties.setSecret("test-auth-key");

        client =
                new KoreaEximExchangeRateClient(koreaEximRestClient, koreaEximProperties);
    }

    @Mock
    ChargeRepository chargeRepository;

    CurrencyCalculator currencyCalculator = new CurrencyCalculator();

    @InjectMocks
    ChargeService chargeService;

    @Test
    void testFetchExchangeRates() {
        String responseBody = """
                [
                  {
                    "cur_unit": "USD",
                    "cur_nm": "미국 달러",
                    "deal_bas_r": "1,400.00",
                    "ttb": "1,386.00",
                    "tts": "1,414.00"
                  }
                ]
                """;


        mockServer.expect(requestTo(
                        containsString("/site/program/financial/exchangeJSON")
                ))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        responseBody,
                        MediaType.APPLICATION_JSON
                ));

        ExchangeRate result = client.fetchRate(
                Currency.USD,
                Currency.KRW,
                ExchangeRateType.DEAL_BASE
        );

        assertEquals(Currency.USD, result.from());
        assertEquals(Currency.KRW, result.to());
        assertEquals(new BigDecimal("1400.00"), result.rate());
        assertEquals(ExchangeRateType.DEAL_BASE, result.type());
        mockServer.verify();
    }

}
