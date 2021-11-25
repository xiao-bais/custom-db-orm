package com.custom.test;

import com.custom.comm.BasicDao;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.handler.JdbcDao;
import com.custom.handler.proxy.SqlReaderExecuteProxy;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

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
//        dbCustomStrategy.setSqlOutPrinting(true);
//        dbCustomStrategy.setSqlOutUpdate(true);
//        dbCustomStrategy.setDbFieldDeleteLogic("state");
//        dbCustomStrategy.setDeleteLogicValue("1");
//        dbCustomStrategy.setNotDeleteLogicValue("0");
        dbDataSource.setDbCustomStrategy(dbCustomStrategy);
//        dbCustomStrategy.setUnderlineToCamel(true);
//        JdbcDao jdbcDao = new JdbcDao(dbDataSource);


        JdbcTest jdbcTest  = new SqlReaderExecuteProxy(dbDataSource).createProxy(JdbcTest.class);
        Employee employee = new Employee();
        employee.setId(5);
        employee.setEmpName("陈工");
        List<Employee> employees = jdbcTest.selectListById(Arrays.asList(1, 5));


    }

}
