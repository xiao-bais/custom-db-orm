package com.home;

import com.custom.comm.utils.CustomApplicationUtil;
import com.home.customtest.dao.CustomTestDao;
import com.home.customtest.dao.StudentDao;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

@MapperScan(basePackages = "com.home.customtest.mapper")
@SpringBootApplication
public class CustomTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomTestApplication.class, args);
    }

}
