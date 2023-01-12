package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.core.JdbcDao;
import com.custom.action.core.JdbcOpDao;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;

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

        List<ChildStudent> studentList = jdbcDao.selectList(Conditions.lambdaQuery(ChildStudent.class)
                .eq(Student::getAge, 13)
                .or(op -> op.notBetween(ChildStudent::getAge, 10, 24))
                .or(op -> op.like(Student::getNickName, "小风"))
                .pageParams(1, 10)
                .exists(Employee.class, x -> x.apply(ChildStudent::getNickName, Employee::getEmpName)
                        .ge(Employee::getAge, 22)
                        .eq(Employee::getState, 0)
                )
                .toDefault().select("a.sex money", "ifnull(max(a.age), 0) maxAge")
                .toLambda().groupBy(ChildStudent::getSex)
        );
        System.out.println("studentList.size() = " + studentList.size());

    }








}
