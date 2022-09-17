package com.home;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.condition.Conditions;
import com.custom.action.condition.DefaultConditionWrapper;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.joiner.core.AbstractJoinConditional;
import com.custom.joiner.core.AbstractJoinWrapper;
import com.custom.joiner.core.LambdaJoinConditional;
import com.custom.joiner.core.LambdaJoinWrapper;
import com.home.customtest.entity.*;

import java.util.List;

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

        long t1 = System.currentTimeMillis();
        List<Province> provinces = jdbcDao.selectList(new Province());
        long t2 = System.currentTimeMillis();
        List<Province> provinces2 = jdbcDao.selectList(new Province());
        long t3 = System.currentTimeMillis();
        System.out.println("t = " + (t2 - t1));
        System.out.println("t2 = " + (t3 - t2));


    }

}
