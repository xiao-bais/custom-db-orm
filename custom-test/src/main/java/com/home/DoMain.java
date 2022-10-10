package com.home;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.jdbc.back.BackResult;
import com.custom.jdbc.transaction.BackResultTransactionProxy;
import com.home.customtest.entity.Aklis;
import com.home.customtest.entity.Student;

import java.util.Arrays;

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


        Student student = jdbcOpDao.selectByKey(Student.class, 7);
        BackResult<Student> backResult = BackResult.execCall(op -> {
            jdbcDao.deleteByKey(Student.class, 56);
//            int a = 1 / 0;
            jdbcDao.deleteByKey(Student.class, 57);
            op.success(student);
        });

        System.out.println("backResult = " + backResult);


    }


}
