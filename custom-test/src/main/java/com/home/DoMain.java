package com.home;

import com.custom.action.core.JdbcDao;
import com.custom.action.core.JdbcOpDao;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;

import java.util.List;

/**
 * @author  Xiao-Bai
 * @since  2021/11/29 12:54
 * @Desc：
 **/
//@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {

        JdbcTestBuilder jdbcTestBuilder = JdbcTestBuilder.builder();
        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();
        MyService helper = new MyServiceImpl();

        List<Student> students = jdbcDao.createChain(Student.class).whereEx(x -> x.eq(Student::getAge, 30)).getList();
        System.out.println("students = " + students);

    }









}
