package com.mips.global.batch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;

@Configuration
public class MipsBatchPolicy {

    @Bean
    public ExponentialBackOffPolicy finnhubBackOffPolicy() {
        ExponentialBackOffPolicy backOffPolicy =
                new ExponentialBackOffPolicy();

        backOffPolicy.setInitialInterval(5_000L); // 최초 재시도 전 5초
        backOffPolicy.setMultiplier(2.0);         // 대기시간 2배 증가
        backOffPolicy.setMaxInterval(30_000L);    // 최대 30초

        return backOffPolicy;
    }

}
