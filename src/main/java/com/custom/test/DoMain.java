package com.custom.test;

import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.jdbc.JdbcDao;
import com.custom.jdbc.JdbcUtils;
import com.custom.utils.CommUtils;
import com.custom.utils.DbPageRows;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
        dbCustomStrategy.setPrintSqlFlag(true);
        dbDataSource.setDbCustomStrategy(dbCustomStrategy);

        JdbcDao jdbcDao = new JdbcDao(dbDataSource);

//        List<SmbmsBill> smbmsBills = jdbcDao.selectList(SmbmsBill.class, "");
//        for (SmbmsBill smbmsBill : smbmsBills) {
//            System.out.println("smbmsBill = " + smbmsBill);
//        }
        DbPageRows<Employee> employeeDbPageRows = jdbcDao.selectPageRows(Employee.class, "", 1, 10);
        System.out.println("employeeDbPageRows = " + employeeDbPageRows);


//        long key = jdbcDao.insertReturnKey(Arrays.asList(stu, stu2));
//        System.out.println("key = " + key);


    }
}
