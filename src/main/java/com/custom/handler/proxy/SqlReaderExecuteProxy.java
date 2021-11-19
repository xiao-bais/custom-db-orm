package com.custom.handler.proxy;

import com.custom.dbconfig.DbDataSource;
import com.custom.handler.DbParserFieldHandler;
import com.custom.handler.SqlExecuteHandler;
import com.custom.test.Employee;
import com.custom.test.JdbcTest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/19 16:45
 * @Desc：用于读取接口上的注解，并生成代理类去执行路径中文件中的内容
 **/
public class SqlReaderExecuteProxy extends SqlExecuteHandler implements InvocationHandler {



    public SqlReaderExecuteProxy(DbDataSource dbDataSource) {
        super(dbDataSource, new DbParserFieldHandler());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        System.out.println("进来去了。。。");


        List<Employee> list = new ArrayList<>();
        list.add(new Employee());
        return list;
    }



}
