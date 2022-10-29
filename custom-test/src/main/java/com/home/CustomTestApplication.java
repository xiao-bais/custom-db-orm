package com.home;

import com.custom.comm.utils.CustomApplicationUtil;
import com.home.customtest.dao.CustomTestDao;
import com.home.customtest.dao.StudentDao;
import com.home.customtest.entity.Employee;
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
        CustomTestDao testDao = CustomApplicationUtil.getBean(CustomTestDao.class);
        Employee empInfoBySet = testDao.getEmpInfoBySet(new HashSet<>(Arrays.asList(22, 34)));

        StudentDao studentDao = CustomApplicationUtil.getBean(StudentDao.class);
        Map<String, Object> byMap = studentDao.getEmpInfoByMap(1, "111");
        System.out.println("empInfoBySet = " + empInfoBySet);
        System.out.println("byMap = " + byMap);
    }

}
