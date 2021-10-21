package com.custom.test;

import com.custom.date.DateTimeUtils;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.jdbc.JdbcDao;
import com.custom.page.AutoPageUtil;
import com.custom.page.DbPageRows;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/7/4
 * @Description
 */
public class DoMain {



    public static void main(String[] args) throws Exception {

        DbDataSource dbDataSource = new DbDataSource();
        dbDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/oneTest?characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8");
        dbDataSource.setDriver("com.mysql.cj.jdbc.Driver");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("123456");
//        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
////        dbCustomStrategy.setPrintSqlFlag(true);
////        dbDataSource.setDbCustomStrategy(dbCustomStrategy);

        JdbcDao jdbcDao = new JdbcDao(dbDataSource);

        jdbcDao.createTables(Classes.class, Person.class );

        List<Person> personList = jdbcDao.selectList(Person.class, "");
        for (Person x : personList) {
            System.out.println("x.toString() = " + x.toString());
        }
//
//        Teacher teacher = jdbcDao.selectOneByKey(Teacher.class, 1);
//        System.out.println("teacher.getName() = " + teacher.getName());
    }
}
