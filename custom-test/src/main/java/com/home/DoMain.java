package com.home;

import com.custom.action.condition.*;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Student;

import java.text.SimpleDateFormat;
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");



        Student student = jdbcDao.selectOne(Student.class, "and a.id = 14");
        student.setName("张三逢");
        jdbcDao.updateSelective(student, Conditions.lambdaQuery(Student.class));


    }

}
