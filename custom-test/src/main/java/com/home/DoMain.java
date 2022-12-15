package com.home;

import com.custom.action.core.*;
import com.custom.jdbc.back.BackResult;
import com.home.customtest.entity.Student;

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
//        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();

        // 开始

        BackResult.execCall(it -> {
            Student student = jdbcDao.selectByKey(Student.class, 13);
            student.setNickName("2222");
            jdbcDao.updateByKey(student);

//            int a = 1/0;

            student.setNickName("333333");
            jdbcDao.updateByKey(student);



        });




    }






}
