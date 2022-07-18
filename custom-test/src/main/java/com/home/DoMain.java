package com.home;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.wrapper.Conditions;
import com.custom.action.wrapper.LambdaConditionWrapper;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;

import java.util.ArrayList;
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

//        List<Student> studentList = jdbcDao.selectList(Conditions.lambdaQuery(Student.class));


       Student childStudent = new Student();
       childStudent.setNickName("15875412659");
       childStudent.selectOneByKey()
        System.out.println("childStudent.getId() = " + childStudent.getId());


    }

}
