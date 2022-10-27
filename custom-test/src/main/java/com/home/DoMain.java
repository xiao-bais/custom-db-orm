package com.home;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.jdbc.condition.SelectMapExecutorModel;
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

        Connection connection = DbConnGlobal.getCurrentConnection(jdbcTestBuilder.getDbDataSource());

        String sql = "select age, concat(count(0), '个') num from student group by age";

        SelectMapExecutorModel<Integer, String> selectMapExecutorModel = new SelectMapExecutorModel<>(sql, true, Integer.class, String.class);
        CustomSqlSession sqlSession = new CustomSqlSession(connection, selectMapExecutorModel);

        Map<Integer, String> maps = jdbcExecutor.selectMap(sqlSession);
        for (Map.Entry<Integer, String> entry : maps.entrySet()) {
            System.out.println("年龄为 ==> " + entry.getKey() + " 的数量有 " + entry.getValue());
        }


    }


}
