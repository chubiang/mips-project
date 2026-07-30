package com.mips;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class KoreaEximExchangeRateTests {


    private RestClient koreaEximRestClient;

    @BeforeEach
    void setUp() {
        koreaEximRestClient = RestClient.builder()
                .baseUrl("https://oapi.koreaexim.go.kr")
                .build();
    }

    @Test
    void realExchangeRateTest() {
        String authKey = System.getenv("KOREA_EXIM_AUTH_KEY");

        String body = koreaEximRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/site/program/financial/exchangeJSON")
                        .queryParam("authkey", authKey)
                        .queryParam("searchdate", "20260730")
                        .queryParam("data", "AP01")
                        .build())
                .retrieve()
                .body(String.class);

        System.out.println("===== 실제 응답 바디 =====");
        System.out.println(body);
    }
}
