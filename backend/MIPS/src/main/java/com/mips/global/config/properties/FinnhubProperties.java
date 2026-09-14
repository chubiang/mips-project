package com.mips.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "finnhub")
public class FinnhubProperties {
    private String api;
    private String websocketUrl;
    private String secret;
    // application 설정: finnhub.subscribe-symbols: [AAPL, MSFT]
    private List<String> subscribeSymbols = List.of("AAPL");
}
