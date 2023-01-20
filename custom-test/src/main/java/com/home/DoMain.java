package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.core.JdbcDao;
import com.custom.action.core.JdbcOpDao;
import com.custom.jdbc.interfaces.TransactionExecutor;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;

import java.math.BigDecimal;
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



//        jdbcOpDao.execTrans(() -> {
//            Student student = jdbcDao.selectByKey(Student.class, 77);
//
//            student.setMoney(BigDecimal.valueOf(7768.28));
//            student.update();
//
//            int a=1/0;
//            student.setName("李氏真英雄");
//            student.update();
//        });




    }








}
