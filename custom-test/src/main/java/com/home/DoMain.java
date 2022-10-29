package com.home;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.jdbc.condition.SelectMapExecutorModel;
import com.custom.jdbc.dbAdapetr.Mysql8Adapter;
import com.custom.jdbc.executor.CustomJdbcExecutor;
import com.custom.jdbc.executor.DefaultCustomJdbcExecutor;
import com.custom.jdbc.session.CustomSqlSession;
import com.custom.jdbc.transaction.DbConnGlobal;

import java.sql.Connection;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
//@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {


        JdbcTestBuilder jdbcTestBuilder = JdbcTestBuilder.builder();
        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();

//        ChildStudent childStudent = jdbcDao.selectOne(Conditions.lambdaQuery(ChildStudent.class));
//        System.out.println("childStudent = " + childStudent);

        CustomJdbcExecutor jdbcExecutor = new DefaultCustomJdbcExecutor(jdbcTestBuilder.getDbCustomStrategy());

        Mysql8Adapter mysql8Adapter = new Mysql8Adapter(jdbcTestBuilder.getDbDataSource(), jdbcExecutor);
        boolean student = mysql8Adapter.existColumn("student", "address_one");
        System.out.println("student = " + student);


    }


}
