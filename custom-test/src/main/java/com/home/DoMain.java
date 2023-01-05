package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.core.*;
import com.custom.comm.utils.CustomUtil;
import com.custom.jdbc.back.BackResult;
import com.home.customtest.entity.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        List<ChildStudent> studentList = jdbcDao.selectList(Conditions.lambdaQuery(ChildStudent.class)
                .eq(Student::getAge, 13)
                .or(op -> op.notBetween(ChildStudent::getAge, 10, 20))
                .or(op -> op.like(Student::getNickName, "小风"))
                .pageParams(1, 10)
                .toDefault().select("a.sex", "ifnull(max(a.age), 0) maxAge")
                .toLambda().groupBy(ChildStudent::getSex)
        );
        System.out.println("studentList.size() = " + studentList.size());


    }






}
