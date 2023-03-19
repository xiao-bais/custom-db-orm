package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.core.JdbcDao;
import com.custom.action.core.JdbcOpDao;
import com.custom.comm.page.DbPageRows;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author  Xiao-Bai
 * @since  2021/11/29 12:54
 * @Desc：
 **/
//@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {

        JdbcTestBuilder jdbcTestBuilder = JdbcTestBuilder.builder();
        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();

        MyService helper = new MyServiceImpl();

        DbPageRows<Map<String, Object>> mapDbPageRows = jdbcDao.selectPageMap(Conditions.lambdaQuery(Student.class).in(Student::getAge, 1, 2, 4, 12, 15).pageParams(1, 3));
        System.out.println("students = " + 1);

    }









}
