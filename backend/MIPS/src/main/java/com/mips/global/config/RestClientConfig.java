package com.mips.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class RestClientConfig {

    private final PortOneSecretProperties portOneSecretProperties;

    private final KoreaEximProperties koreaEximProperties;

    public RestClientConfig(PortOneSecretProperties portOneSecretProperties, KoreaEximProperties koreaEximProperties) {
        this.portOneSecretProperties = portOneSecretProperties;
        this.koreaEximProperties = koreaEximProperties;
    }

    @Bean
    public RestClient portoneRestClient() {
        log.info("portoneRestClient {} ", portOneSecretProperties.getWebhook());
        return RestClient.builder()
                .baseUrl("https://api.portone.io") // 포트원 V2 기본 주소
                .defaultHeader("Authorization", "PortOne " + portOneSecretProperties.getSecret())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
    @Bean
    public RestClient koreaEximRestClient() {
        log.info("koreaEximRestClient {} ", koreaEximProperties.getExchangeUrl());
        return RestClient.builder()
                .baseUrl(koreaEximProperties.getExchangeUrl())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }



}
