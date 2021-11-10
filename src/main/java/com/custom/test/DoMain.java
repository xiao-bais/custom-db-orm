package com.custom.test;

import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.handler.JdbcDao;

import java.util.Arrays;
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
        dbCustomStrategy.setDbFieldDeleteLogic("state");
        dbCustomStrategy.setDeleteLogicValue("1");
        dbCustomStrategy.setNotDeleteLogicValue("0");
        dbDataSource.setDbCustomStrategy(dbCustomStrategy);
        dbCustomStrategy.setUnderlineToCamel(true);
        JdbcDao jdbcDao = new JdbcDao(dbDataSource);

//        List<Employee> employeeList = jdbcDao.selectList(Employee.class, "and age > 22");
//        System.out.println("employee = " + employee);
        String sql = "select a.`id` `id`, a.`emp_name` emp_name, a.`sex` `sex`, a.`age` `age`, a.`address` `address`, a.`birthday` `birthday`, a.`state` `state` \n" +
                "from employee a   \n" +
                "where `state` = 0  and a.age = 23 ";
        Employee employee = jdbcDao.selectOneByCondition(Employee.class, "and a.age = 23");
        System.out.println("employee = " + employee.toString());


    }
}
