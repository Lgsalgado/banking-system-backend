package com.bank.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.bank.accounts.domain.repository")
public class MsCuentasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsCuentasApplication.class, args);
    }

}
