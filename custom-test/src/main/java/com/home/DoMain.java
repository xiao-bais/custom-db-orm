package com.home;

import com.custom.action.condition.*;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.home.customtest.entity.ChildStudent;
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

        List<Student> studentList = jdbcDao.selectList(Student.class, "and a.id = 13");

//        Student student = jdbcDao.selectOne(Student.class, "and a.sex = ? and a.id = 14", false);
//        student.setName("张三逢");
//        jdbcDao.updateSelective(student);


    }

}
