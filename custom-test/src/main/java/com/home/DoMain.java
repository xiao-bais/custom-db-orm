package com.home;

import com.custom.action.sqlparser.*;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Employee;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
//@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {


        JdbcTestBuilder jdbcTestBuilder = JdbcTestBuilder.builder();
        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();


        String[] employees = jdbcDao.selectArrays(String.class, "select birthday from employee");
        System.out.println(Arrays.toString(employees));


    }




}
