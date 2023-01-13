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
                .eq(Student::getNickName, "aaaa")
                .ge(Student::getAge, 22)
                .between(Student::getAge, 24, 30)
                .or(x -> x.eq(Student::getAddress, "湖南长沙"))
                .toDefault()
                .select("case when a.sex = 1 then '男' else '女' end caseAge")
                .toLambda()
                .orderByAsc(Student::getMoney)
        );
        System.out.println("studentList.size() = " + studentList.size());

    }








}
