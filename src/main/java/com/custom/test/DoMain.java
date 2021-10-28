package com.custom.test;

import com.custom.dbconfig.DbDataSource;
import com.custom.handler.JdbcDao;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/28 9:18
 * @Desc：
 **/
public class DoMain {

    public static void main(String[] args) throws Exception {

        DbDataSource dbDataSource = new DbDataSource();
        dbDataSource.setUrl("jdbc:mysql://127.0.0.1/hos?characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8");
        dbDataSource.setDriver("com.mysql.cj.jdbc.Driver");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("123456");

        JdbcDao jdbcDao = new JdbcDao(dbDataSource);

        //创建一个表
        jdbcDao.createTables(Employee.class);

    }
}
