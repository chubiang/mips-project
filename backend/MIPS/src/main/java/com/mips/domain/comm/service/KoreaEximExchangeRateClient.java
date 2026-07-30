package com.mips.domain.comm.service;

import com.mips.domain.comm.dto.ExchangeRate;
import com.mips.domain.comm.dto.KoreaEximExchangeRateResponse;
import com.mips.domain.comm.enums.Currency;
import com.mips.domain.comm.enums.ExchangeRateType;
import com.mips.global.config.KoreaEximProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
@Component
public class KoreaEximExchangeRateClient implements ExchangeRateClient {

    private final RestClient koreaEximRestClient;
    private final KoreaEximProperties koreaEximProperties;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    public KoreaEximExchangeRateClient(@Qualifier("koreaEximRestClient") RestClient koreaEximRestClient,
                                       KoreaEximProperties  koreaEximProperties) {
        this.koreaEximRestClient = koreaEximRestClient;
        this.koreaEximProperties = koreaEximProperties;
    }

    @Override
    public ExchangeRate fetchRate(Currency from, Currency to, ExchangeRateType exchangeRateType) {
        // 1. 외부 API 호출
        KoreaEximExchangeRateResponse[] dataList = fetchExchangeRates(LocalDate.now());
        log.info("dataList {}", Arrays.toString(dataList));
        log.info("from {}, to {} ExchangeRateType", from, to, exchangeRateType);
        // 2. USD/KRW, JPY/KRW 환율 응답 파싱
        for (KoreaEximExchangeRateResponse data : dataList) {
            log.info("data {}", data);
            if (from.getUnit().equals(data.curUnit())) {
                String rawRate = switch (exchangeRateType) {
                    case DEAL_BASE -> data.dealBasR();
                    case CUSTOMER_BUYS_FOREIGN -> data.tts();
                    case CUSTOMER_SELLS_FOREIGN -> data.ttb();
                };

                return new ExchangeRate(
                        from,
                        to,
                        parseRate(rawRate),
                        exchangeRateType,
                        LocalDate.now(),
                        to.getScale()
                );
            }
        }

        throw new IllegalArgumentException("환율 정보를 찾을 수 없습니다: " + from + " -> " + to);
    }

    private BigDecimal parseRate(String value) {
        return new BigDecimal(value.replace(",", ""));
    }

    /* 한국수출입은행 특정일자 환율정보 조회 */
    private KoreaEximExchangeRateResponse[] fetchExchangeRates(
            LocalDate searchDate
    ) {
        // [data 구분값] AP01 : 환율, AP02 : 대출금리, AP03 : 국제금리
        KoreaEximExchangeRateResponse[] response =
                koreaEximRestClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/site/program/financial/exchangeJSON")
                                .queryParam(
                                        "authkey",
                                        koreaEximProperties.getSecret()
                                )
                                .queryParam(
                                        "searchdate",
                                        searchDate.format(DATE_FORMATTER)
                                )
                                .queryParam("data", "AP01")
                                .build())
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(KoreaEximExchangeRateResponse[].class);

        log.info("response {}", response);

        return response == null
                ? new KoreaEximExchangeRateResponse[0]
                : response;
    }

}
