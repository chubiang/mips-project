package com.mips.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class RestClientConfig {

    private final PortOneSecretProperties properties;

    public RestClientConfig(PortOneSecretProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RestClient portoneRestClient() {
        log.info("portoneRestClient {} ", properties.getWebhook());
        return RestClient.builder()
                .baseUrl("https://api.portone.io") // 포트원 V2 기본 주소
                .defaultHeader("Authorization", "PortOne " + properties.getSecret())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
