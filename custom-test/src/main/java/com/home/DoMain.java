package com.home;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.custom.action.condition.Conditions;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.jdbc.back.BackResult;
import com.custom.jdbc.transaction.BackResultTransactionProxy;
import com.home.customtest.entity.Aklis;
import com.home.customtest.entity.Province;
import com.home.customtest.entity.Student;
import com.home.customtest.entity.ThreadDemo;
import org.springframework.util.StopWatch;

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
//        StopWatch stopWatch = new StopWatch("jdbcDao");
//        stopWatch.start();
        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
//        stopWatch.stop();
//        System.out.println("stopWatch.getTotalTimeMillis() = " + stopWatch.getTotalTimeMillis());
//        StopWatch stopWatch2 = new StopWatch("jdbcOpDao");
//        stopWatch2.start();
        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();
//        stopWatch2.stop();
//        System.out.println("stopWatch2.getTotalTimeMillis() = " + stopWatch2.getTotalTimeMillis());

//        jdbcDao.selectListBySql(Student.class, "select * from student a where a.id = ?", 8);
//        jdbcDao.selectListBySql(Student.class, "select * from student a where a.id = ?", 9);


        StopWatch stopWatch = new StopWatch("exec");
        stopWatch.start();
        BackResult.execCall(op -> {
            jdbcOpDao.selectListBySql(Student.class, "select * from student a where a.id = ?", 11);
            jdbcOpDao.selectListBySql(Student.class, "select * from student a where a.id = ?", 13);
        });
        stopWatch.stop();
        System.out.println("stopWatch.getTotalTimeMillis() = " + stopWatch.getTotalTimeMillis());

        StopWatch stopWatch2 = new StopWatch("exec");
        stopWatch2.start();
        BackResult.execCall(op -> {
            jdbcOpDao.selectListBySql(Student.class, "select * from student a where a.id = ?", 11);
            jdbcOpDao.selectListBySql(Student.class, "select * from student a where a.id = ?", 13);
        });
        stopWatch2.stop();
        System.out.println("stopWatch2.getTotalTimeMillis() = " + stopWatch2.getTotalTimeMillis());






    }


}
