package com.home;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.custom.action.condition.Conditions;
import com.custom.action.condition.DefaultConditionWrapper;
import com.custom.action.condition.LambdaConditionWrapper;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
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


        System.out.println(1);
        Student student = jdbcOpDao.selectByKey(Student.class, 11);
        System.out.println(2);
        List<Student> studentList = jdbcOpDao.selectList(Student.class, "and a.name = ?", "李重阳");
        System.out.println(3);


    }


}
