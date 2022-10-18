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
        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();

        System.out.println(1);
        jdbcOpDao.selectList(Student.class, "and a.id = ?", 3);
        System.out.println(2);
        jdbcOpDao.selectList(Province.class, "a.id = ?", 4);
        System.out.println(3);
        jdbcOpDao.selectList(Student.class, "and a.id = ?", 5);
        System.out.println(4);
        jdbcOpDao.selectList(Student.class, "and a.age = ?", 22);
        System.out.println(5);






    }


}
