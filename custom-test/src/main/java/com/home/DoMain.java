package com.home;

import com.custom.action.core.*;
import com.custom.jdbc.back.BackResult;
import com.home.customtest.entity.*;

import java.util.*;

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

//        List<ChildStudent> studentList = jdbcDao.selectList(Conditions.lambdaQuery(ChildStudent.class)
//                .eq(Student::getAge, 13)
//                .or(op -> op.notBetween(ChildStudent::getAge, 10, Arrays.asList(11,12,13)))
//                .or(op -> op.like(Student::getNickName, "小风"))
//                .pageParams(1, 10)
//                .toDefault().select("a.sex money", "ifnull(max(a.age), 0) maxAge")
//                .toLambda().groupBy(ChildStudent::getSex)
//        );
//        System.out.println("studentList.size() = " + studentList.size());

        List<Student> studentList = jdbcDao.selectList(Student.class, "and a.name = ? ", "李雨");

        jdbcOpDao.execTrans(() -> {
            Employee employee = jdbcOpDao.selectByKey(Employee.class, 10);
            employee.setEmpName("李小宝");
            jdbcOpDao.updateByKey(employee);
            int a = 1/0;
            employee.setEmpName("李大宝");
            jdbcOpDao.updateByKey(employee);
        });



    }








}
