package com.home;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.joiner.core.JoinStyleWrapper;
import com.custom.joiner.core.LambdaJoinStyleWrapper;
import com.home.customtest.entity.*;

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

        JoinStyleWrapper<Student> joinStyleWrapper = new LambdaJoinStyleWrapper<>(Student.class);



    }

}
