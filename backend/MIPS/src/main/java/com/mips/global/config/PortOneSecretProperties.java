package com.mips.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "portone.secret")
public class PortOneSecretProperties {

    private String storeId;
    private String secret;
    private String api;
    private String webhook;
}
