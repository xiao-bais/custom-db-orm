package com.custom.test;

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
        dbDataSource.setUrl("jdbc:mysql://127.0.0.1/smbms?characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8");
        dbDataSource.setDriver("com.mysql.cj.jdbc.Driver");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("123456");

        JdbcDao jdbcDao = new JdbcDao(dbDataSource);

//        List<Employee> list = new LinkedList<>();
//        for (int i = 1; i < 1000; i++) {
//            list.add(new Employee("张三"+i, i % 2 > 0, 25, new BigDecimal(5000 + i)));
//        }

//        int insert = jdbcDao.insert(list);
//        System.out.println("insert = " + insert);

        List<Employee> employeeList = jdbcDao.selectList(Employee.class, "and a.emp_name like '%张%'");
        AutoPageUtil<Employee> pageUtil = new AutoPageUtil<>(1,7, employeeList);
        System.out.println("pageUtil = " + pageUtil);


//        long key = jdbcDao.insertReturnKey(Arrays.asList(stu, stu2));
//        System.out.println("key = " + key);


    }
}
