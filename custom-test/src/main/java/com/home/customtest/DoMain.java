package com.home.customtest;

import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.handler.JdbcDao;
import com.custom.sqlparser.TableSqlBuilder;
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
        DbDataSource dbDataSource = new DbDataSource();
        dbDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("123456");

        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
//        dbCustomStrategy.setMapperScanEnable(true);
//        dbCustomStrategy.setPackageScans(new String[]{"com.custom.customtest.dao"});

        JdbcDao jdbcDao = new JdbcDao(dbDataSource, dbCustomStrategy);
        long time = new Date().getTime();
        TableSqlBuilder<Employee> tableSqlBuilder = new TableSqlBuilder<>(Employee.class);

        long time1 = new Date().getTime();
        String selectSql = tableSqlBuilder.getSelectSql();
        Employee employee = jdbcDao.selectOneBySql(Employee.class, selectSql + " where a.id = 1");
        long time2 = new Date().getTime();

        Employee employee1 = jdbcDao.selectOneByKey(Employee.class, 1);
        long time3 = new Date().getTime();

        System.out.println("time = " + (time1-time));
        System.out.println("time = " + (time2-time1));
        System.out.println("time = " + (time3-time2));

        System.out.println("employee = " + employee);
        System.out.println("employee1 = " + employee1);

    }
}
