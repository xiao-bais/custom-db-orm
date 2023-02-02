package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.core.JdbcDao;
import com.custom.action.core.JdbcOpDao;
import com.home.customtest.entity.Student;

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


        long start = System.currentTimeMillis();
        System.out.println("start = " + start);
        List<Object> objectList = jdbcDao.selectObjs(Conditions.lambdaQuery(Student.class).onlyPrimary().eq(Student::getAddress, "湖南长沙").le(Student::getAge, 15).select(Student::getName));
        System.out.println("objectList = " + objectList);


    }








}
