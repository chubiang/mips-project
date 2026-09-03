package com.mips.domain.stock.service;

import com.mips.domain.stock.dto.FinnhubQuoteResponse;
import com.mips.domain.stock.dto.Nasdaq100Response;
import com.mips.domain.stock.dto.NasdaqDataRow;
import com.mips.domain.stock.entity.SecurityMaster;
import com.mips.domain.stock.entity.SecurityQuote;
import com.mips.domain.stock.enums.Exchange;
import com.mips.domain.stock.enums.SecurityType;
import com.mips.domain.stock.repository.SecurityMasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class NasdaqSyncService {

    private final RestClient nasdaq100RestClient;
    private final RestClient finnhubRestClient;
    private final SecurityMasterRepository securityMasterRepository;


    public NasdaqSyncService(@Qualifier("nasdaq100RestClient") RestClient nasdaq100RestClient,
                             @Qualifier("finnhubRestClient") RestClient finnhubRestClient,
                             SecurityMasterRepository securityMasterRepository) {
        this.nasdaq100RestClient = nasdaq100RestClient;
        this.finnhubRestClient = finnhubRestClient;
        this.securityMasterRepository = securityMasterRepository;
    }


    /**
     * 나스닥상장 목록 API조회 및 동기화 하기
     * https://api.nasdaq.com/api/quote/list-type/nasdaq100
     **/
    public void syncNasdaq100() {
        Nasdaq100Response response = nasdaq100RestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/quote/list-type/nasdaq100").build())
                .retrieve()
                .body(Nasdaq100Response.class);

        List<NasdaqDataRow> nasdaqList = response.data().data().rows();
        Map<String, NasdaqDataRow> tickerMap =  nasdaqList.stream()
                .collect(Collectors.toMap(
                        NasdaqDataRow::symbol,
                        Function.identity())
                );
        List<SecurityMaster> scrtMaster = securityMasterRepository.findAll();
        // 기존 나스닥 확인
        for (SecurityMaster mm : scrtMaster) {
            String ticker = mm.getTicker();
            // 동일한 키가 존재할 경우.
            if (tickerMap.containsKey(ticker))
            {
                NasdaqDataRow row = tickerMap.get(ticker);

                if (row == null) {
                    continue;
                }

                mm.setSecurityMaster(
                        row.symbol(),
                        Exchange.NASDAQ,
                        row.companyName()
                );
                tickerMap.remove(ticker);
            }
        }
        // 신규 나스닥 상장일경우
        List<SecurityMaster> newSecurities =
                tickerMap.values().stream()
                        .map(row ->
                                SecurityMaster.builder()
                                        .ticker(row.symbol())
                                        .exchange(Exchange.NASDAQ)
                                        .securityType(SecurityType.STOCK)
                                        .name(row.companyName())
                                        .build()
                        )
                        .toList();

        // 신규 Entity는 저장
        securityMasterRepository.saveAll(newSecurities);
    }

    public List<SecurityMaster> getNasdaqList() {
        return securityMasterRepository.findAllByExchangeAndIsActive(Exchange.NASDAQ, true);
    }

    // 미국 주식만 조회 가능해서 여기다가 씀 T_T
    public SecurityQuote getStockPrice(SecurityMaster securityMaster) {
        // 무료버전 사용중이라서 요청 갯수 제한때문에 5초 단위로 제한
        waitForRateLimit();
        log.info("Finnhub API 호출시작! - {}", securityMaster.getTicker());
        // Finnhub에 ticker의 시가 조회
        FinnhubQuoteResponse response = finnhubRestClient.get()
                                                        .uri(uriBuilder -> uriBuilder
                                                                .path("/api/v1/quote")
                                                                .queryParam("symbol", securityMaster.getTicker())
                                                                .build())
                                                        .retrieve()
                                                        .body(FinnhubQuoteResponse.class);
        if (response == null) {
            return null;
        }

        return new SecurityQuote(securityMaster,
                                response.c(),
                                response.d(),
                                response.dp(),
                                response.h(),
                                response.l(),
                                response.o(),
                                response.pc(),
                                response.t());
    }

    private void waitForRateLimit() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Finnhub API 호출 대기 중 interrupt 발생️☠️☠️☠️☠️",
                    e
            );
        }
    }

}
