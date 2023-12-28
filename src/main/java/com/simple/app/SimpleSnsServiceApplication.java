package com.simple.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SimpleSnsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleSnsServiceApplication.class, args);
    }

}
