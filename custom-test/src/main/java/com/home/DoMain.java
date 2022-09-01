package com.home;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.joiner.core.AbstractJoinConditional;
import com.custom.joiner.core.AbstractJoinWrapper;
import com.custom.joiner.core.LambdaJoinConditional;
import com.custom.joiner.core.LambdaJoinWrapper;
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

        LambdaJoinWrapper<Student> joinStyleWrapper = new LambdaJoinWrapper<>(Student.class);
        LambdaJoinConditional<Province> joinConditional = new LambdaJoinConditional<>(Province.class).eq(Province::getId, Student::getProId);
        joinStyleWrapper.leftJoin(joinConditional);
        joinStyleWrapper.eq(Employee::getEmpName, "aaa");
        String sqlAction = joinConditional.formatJoinSqlAction();
        System.out.println("sqlAction = " + sqlAction);
//        joinStyleWrapper.leftJoin(Province.class, join -> join.eq(Province::getId, Student::getProId).eq(Province::getName, Student::getName));




    }

}
