package com.home;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.wrapper.Conditions;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {

        JdbcDao jdbcDao = JdbcTestBuilder.builder().getJdbcDao();

        List<Student> studentList = jdbcDao.selectList(Conditions.lambdaQuery(Student.class));

        studentList = studentList.stream().filter(x -> x != null).collect(Collectors.toList());


    }

}
