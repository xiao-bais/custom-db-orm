package com.home;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.condition.Conditions;
import com.custom.action.condition.LambdaConditionWrapper;
import com.custom.action.sqlparser.*;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

//        jdbcDao.selectOne(new LambdaConditionWrapper<>(Student.class).eq(Student::getNickName, "zhangsan").onlyPrimary());


        Map<Integer, Integer> objectMap = jdbcDao.selectMap(Conditions.query(Employee.class)
                .select("age", "count(a.age)")
                .groupBy("a.age")
                .onlyPrimary(),
                Integer.class, Integer.class
        );

//        Map<Integer, Integer> objectMap1 = jdbcDao.selectMap(Conditions.query(Employee.class)
//                        .select("age", "count(a.age)")
//                        .groupBy("a.age"),
//                Integer.class, Integer.class
//        );

        Employee employee = jdbcDao.selectByKey(Employee.class, 12);

        System.out.println(objectMap.toString());


    }




}
