package com.mips;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EntityScan(basePackages = "com.mips.domain")
@EnableJpaRepositories(basePackages = "com.mips.domain")
@EnableScheduling
@SpringBootApplication
public class MipsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MipsApplication.class, args);
    }

}
