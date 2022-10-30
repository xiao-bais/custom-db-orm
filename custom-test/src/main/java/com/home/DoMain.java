package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.comm.utils.CustomUtil;
import com.custom.jdbc.condition.SelectMapExecutorModel;
import com.custom.jdbc.dbAdapetr.Mysql8Adapter;
import com.custom.jdbc.executor.CustomJdbcExecutor;
import com.custom.jdbc.executor.DefaultCustomJdbcExecutor;
import com.custom.jdbc.session.CustomSqlSession;
import com.custom.jdbc.transaction.DbConnGlobal;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Student;

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

        Student student = new Student();
        student.setName("张丹");

        boolean isSuccess = CustomUtil.writeFieldValue("李四", student, "name", String.class);
        System.out.println("isSuccess = " + isSuccess);

        boolean isSucc = CustomUtil.writeFieldValue(23, student, "age", Integer.class);
        System.out.println("isSucc = " + isSucc);


    }


}
