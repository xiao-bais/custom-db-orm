package com.home;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.custom.action.condition.ConditionWrapper;
import com.custom.action.condition.Conditions;
import com.custom.action.condition.DefaultConditionWrapper;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.sqlparser.*;
import com.custom.joiner.core.AbstractJoinConditional;
import com.custom.joiner.core.AbstractJoinWrapper;
import com.custom.joiner.core.LambdaJoinConditional;
import com.custom.joiner.core.LambdaJoinWrapper;
import com.home.customtest.entity.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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


        Employee employee = jdbcDao.selectByKey(Employee.class, 11);
        employee.setId(null);

        employee.setEmpName("新增的数据...");

        HandleInsertSqlBuilder<Employee> insertSqlBuilder = new HandleInsertSqlBuilder<>(Employee.class);
        insertSqlBuilder.setEntityList(Collections.singletonList(employee));
        String targetSql = insertSqlBuilder.createTargetSql();

        jdbcDao.executeSql(targetSql, insertSqlBuilder.getSqlParams());

        System.out.println("targetSql = " + targetSql);

    }

}
