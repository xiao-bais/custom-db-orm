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
//        dbDataSource.setUrl("jdbc:mysql://127.0.0.1/hos?characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8");
        dbDataSource.setDriver("com.mysql.cj.jdbc.Driver");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("123456");
        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
        dbCustomStrategy.setPackageScans(new String[]{"com.custom.test"});

//        //是否打印执行的sql 默认false
        dbCustomStrategy.setSqlOutPrinting(true);
        dbCustomStrategy.setSqlOutUpdate(true);
        dbCustomStrategy.setDbFieldDeleteLogic("state");
        dbCustomStrategy.setDeleteLogicValue("1");
        dbCustomStrategy.setNotDeleteLogicValue("0");
        dbCustomStrategy.setUnderlineToCamel(true);
        JdbcDao jdbcDao = new JdbcDao(dbDataSource, dbCustomStrategy);

        List<Employee> employees = jdbcDao.selectList(Employee.class, null);

//        JdbcTest jdbcTest  = new SqlReaderExecuteProxy(dbDataSource, dbCustomStrategy).createProxy(JdbcTest.class);
//        Employee employee = new Employee();
//        employee.setId(5);
//        employee.setEmpName("陈工");
//        List<Employee> employees = jdbcTest.selectListById(Arrays.asList(1, 5));


    }

}
