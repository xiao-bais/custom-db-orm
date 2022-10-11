package com.home;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.jdbc.back.BackResult;
import com.custom.jdbc.transaction.BackResultTransactionProxy;
import com.home.customtest.entity.Aklis;
import com.home.customtest.entity.Province;
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

        BackResult<Object> objectBackResult = BackResult.execCall(x -> {
            Student student = jdbcDao.selectByKey(Student.class, 11);
            Student student1 = jdbcDao.selectByKey(Student.class, 13);
        });

        Student student = jdbcDao.selectByKey(Student.class, 14);
        Student student2 = jdbcDao.selectByKey(Student.class, 15);

        BackResult<Object> objectBackResult2 = BackResult.execCall(x -> {
            Student student22 = jdbcDao.selectByKey(Student.class, 11);
            Student student111 = jdbcDao.selectByKey(Student.class, 13);
        });

        System.out.println("objectBackResult = " + objectBackResult);


    }


}
