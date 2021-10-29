package com.custom.test;

import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.handler.JdbcDao;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/28 9:18
 * @Desc：
 **/
public class DoMain {

    public static void main(String[] args) throws Exception {

        DbDataSource dbDataSource = new DbDataSource();
        dbDataSource.setUrl("jdbc:mysql://127.0.0.1/smbms?characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8");
        dbDataSource.setDriver("com.mysql.cj.jdbc.Driver");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("123456");
        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
        //是否打印执行的sql 默认false
        dbCustomStrategy.setSqlOutPrinting(true);
        dbDataSource.setDbCustomStrategy(dbCustomStrategy);

        JdbcDao jdbcDao = new JdbcDao(dbDataSource);

        //创建一个表
        jdbcDao.createTables(Employee.class);

        Employee employee = jdbcDao.selectOneByCondition(Employee.class, "and a.id = ?", 5);
        // List<Employee> employeeList2 = jdbcDao.selectList(Employee.class, "and a.id = 5");
        System.out.println("employee = " + employee.toString());

    }
}
