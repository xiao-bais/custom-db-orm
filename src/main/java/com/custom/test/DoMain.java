package com.custom.test;

import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.handler.JdbcDao;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/28 9:18
 * @Desc：
 **/
public class DoMain {

    public static void main(String[] args) throws Exception {

        long time1 = new Date().getTime();
        DbDataSource dbDataSource = new DbDataSource();
        dbDataSource.setUrl("jdbc:mysql://127.0.0.1/hos?characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8");
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
        long time2 = new Date().getTime();
        System.out.println("初始化:" + (time2 - time1));
//        List<Employee> employeeList = jdbcDao.selectList(Employee.class, "and age > 22");
//        System.out.println("employee = " + employee);
        String sql = "select a.`id` `id`, a.`emp_name` emp_name, a.`sex` `sex`, a.`age` `age`, a.`address` `address`, a.`birthday` `birthday`, a.`state` `state` \n" +
                "from employee a   \n" +
                "where `state` = 0  and a.age = 23 ";
//        Employee employee = new Employee();
//        employee.setEmpName("李四111");
//        employee.setAddress(123454);
//        employee.setAge(25);
//        employee.setBirthday(new Date());
//        employee.setSex(false);
//        jdbcDao.insert(Collections.singletonList(employee));

//        Employee employee = jdbcDao.selectOneByKey(Employee.class, "");
        long time3 = new Date().getTime();
        System.out.println("执行完：" + (time3 - time2));

    }
}
