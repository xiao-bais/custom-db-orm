package com.home.customtest;

import com.custom.sqlparser.BaseTableSqlBuilder;
import com.home.customtest.entity.Employee;

import java.util.Date;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
public class DoMain {


    public static void main(String[] args) throws Exception {

        long start = new Date().getTime();
        BaseTableSqlBuilder<Employee> baseTableBuilder = new BaseTableSqlBuilder<>(Employee.class);
        String tableSql = baseTableBuilder.createTableSql();
        System.out.println("tableSql = " + tableSql);
        long time1 = new Date().getTime();
        System.out.println("time1 = " + (time1 - start));

//        long start = new Date().getTime();
//        DbDataSource dbDataSource = new DbDataSource();
//        dbDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC");
//        dbDataSource.setUsername("root");
//        dbDataSource.setPassword("123456");
//
//        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
//        dbCustomStrategy.setMapperScanEnable(true);
//        dbCustomStrategy.setPackageScans(new String[]{"com.custom.customtest.dao"});
//
//        JdbcDao jdbcDao = new JdbcDao(dbDataSource, dbCustomStrategy);
//        jdbcDao.createTables(Employee.class);
//
//        CustomDao customDao = new SqlReaderExecuteProxy(dbDataSource, dbCustomStrategy).createProxy(CustomDao.class);
//        long time = new Date().getTime();
//        String s = customDao.selectOneByCond(1,25, "age");
//        long time1 = new Date().getTime();
//        System.out.println("s = " + s);
//        System.out.println("time = " + (time1-time));
//        System.out.println("time2 = " + (time-start));


    }
}
