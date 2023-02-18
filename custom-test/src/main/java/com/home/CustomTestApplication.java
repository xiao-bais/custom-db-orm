package com.home;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.home.customtest.mapper")
@SpringBootApplication
public class CustomTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomTestApplication.class, args);
    }

}
