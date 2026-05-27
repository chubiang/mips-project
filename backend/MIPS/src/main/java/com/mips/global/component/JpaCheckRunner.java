package com.mips.global.component;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.core.env.Environment;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class JpaCheckRunner implements ApplicationRunner {

    private final Environment env;
    private final EntityManagerFactory emf;


    public JpaCheckRunner(Environment env, EntityManagerFactory emf) {
        this.env = env;
        this.emf = emf;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("===== JPA SETTINGS CHECK =====");
        System.out.println("spring.jpa.hibernate.ddl-auto = "
                + env.getProperty("spring.jpa.hibernate.ddl-auto"));

        System.out.println("hibernate.hbm2ddl.auto = "
                + env.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto"));

        System.out.println("jakarta schema action = "
                + env.getProperty("spring.jpa.properties.jakarta.persistence.schema-generation.database.action"));

        System.out.println("datasource.url = "
                + env.getProperty("spring.datasource.url"));

        System.out.println("managed entity count = "
                + emf.getMetamodel().getEntities().size());

        emf.getMetamodel().getEntities().forEach(e ->
                System.out.println(e.getName() + " / " + e.getJavaType().getName())
        );

        System.out.println("==============================");

    }
}
