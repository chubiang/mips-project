package com.mips.global.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableEncryptableProperties
public class JasyptConfig {
    // 암호화/복호화에 쓸 '마스터 키' (yml에서 주입받음)
    @Value("${Jasypt.encryptor.password}")
    private String encryptKey;

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        config.setPassword(encryptKey); // 마스터 키 세팅
        config.setAlgorithm("PBEWithMD5AndDES"); // 암호화 알고리즘
        config.setKeyObtentionIterations("1000"); // 암호화 키를 얻기 위해 반복하는 횟수
        config.setPoolSize("1"); // 암호화 요청의 pool 크기
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setStringOutputType("base64"); // Base64 인코딩

        encryptor.setConfig(config);
        return encryptor;
    }
}
