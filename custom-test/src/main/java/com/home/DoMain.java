package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.core.*;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;

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

        jdbcDao.selectPage(Conditions.query(Student.class).eq("name", "11111").orderByAsc("age").pageParams(1, 2));
    }






}
