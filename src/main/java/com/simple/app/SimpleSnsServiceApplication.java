package com.simple.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@SpringBootApplication
public class SimpleSnsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleSnsServiceApplication.class, args);
    }

}
