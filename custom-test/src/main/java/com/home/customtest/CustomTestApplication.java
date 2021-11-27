package com.home.customtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.custom","com.home"})
public class CustomTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomTestApplication.class, args);
    }

}
