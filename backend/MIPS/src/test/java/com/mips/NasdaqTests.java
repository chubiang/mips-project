package com.mips;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mips.domain.stock.dto.Nasdaq100Response;
import com.mips.domain.stock.dto.NasdaqDataRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class NasdaqTests {
    /*
     * NASDAQ100 조회 및 JSON 객체변환 테스트
     */

    private RestClient nasdaq100RestClient;

    private RestClient finnhubRestClient;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/test/resources/test-config.properties")) {
            props.load(fis);

            // 읽어온 Properties를 시스템 속성(System Property)으로 복사 등록
            props.forEach((key, value) -> System.setProperty((String) key, (String) value));

            nasdaq100RestClient = RestClient.builder()
                    .baseUrl(System.getProperty("nasdaq.top100-api")+"/api/quote/list-type/nasdaq100")
                    .defaultHeader("Content-Type", "application/json")
                    .build();

            finnhubRestClient = RestClient.builder()
                    .baseUrl(System.getProperty("finnhub.api"))
                    .defaultHeader("Content-Type", "application/json")
                    .defaultHeader("X-Finnhub-Token", System.getProperty("finnhub.secret"))
                    .build();

        } catch (IOException e) {
            System.err.println("설정 파일을 읽지 못했습니다: " + e.getMessage());
        }
    }

    @Test
    void getNasdaqListTest() throws JsonProcessingException {
        Nasdaq100Response parseData = nasdaq100RestClient.get()
                .retrieve()
                .body(Nasdaq100Response.class);

//        System.out.println("===== 실제 응답 바디 =====");
//        System.out.println(body);
//
//        Nasdaq100Response parseData = objectMapper.readValue(body, Nasdaq100Response.class);

        Map<String, NasdaqDataRow> map = parseData.data().data().rows().stream()
                .collect(Collectors.toMap(NasdaqDataRow::symbol, Function.identity()))
        ;

        map.forEach((k,v)->{
            System.out.println(k +":"+ v);
        });
    }

    @Test
    void getFinnhubQuoteTest() throws JsonProcessingException {
        String testTicker = "AAPL";

        String data = finnhubRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/quote")
                        .queryParam("symbol", testTicker)
                        .build())
                .retrieve()
                .body(String.class);


        System.out.println("========Finnhub=======");
        System.out.println("===== 실제 응답 바디 =====");
        System.out.println(data);
    }
}
