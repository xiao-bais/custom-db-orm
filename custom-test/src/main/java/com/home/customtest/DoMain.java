package com.home.customtest;

import com.custom.comm.page.DbPageRows;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.sqlparser.CustomDao;
import com.custom.wrapper.ConditionEntity;
import com.home.customtest.entity.Dept;
import com.home.customtest.entity.Employee;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
public class DoMain {


    public static void main(String[] args) throws Exception {

        // 数据库连接配置
        DbDataSource dbDataSource = new DbDataSource();
        dbDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("123456");

        // 增删改查映射策略配置
        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
        dbCustomStrategy.setSqlOutPrinting(true);
        dbCustomStrategy.setSqlOutUpdate(true);
        dbCustomStrategy.setUnderlineToCamel(true);
        dbCustomStrategy.setDbFieldDeleteLogic("state");
        dbCustomStrategy.setDeleteLogicValue("1");
        dbCustomStrategy.setNotDeleteLogicValue("0");

//        String selectSql = "select a.name, a.age, a.sex from employee a";
//        int select = selectSql.indexOf("select") + 6;
//        int from = selectSql.indexOf("from") - 1;
//        String substring = selectSql.substring(select, from);
//        System.out.println("select = " + substring);

        CustomDao customDao = new CustomDao(dbDataSource, dbCustomStrategy);
        ConditionEntity<Employee> conditionEntity = new ConditionEntity<>(Employee.class);
//        conditionEntity.like("emp_name", "工")
//                .select("emp_name", "age", "name", "dept.name")
//                .eq("sex", true)
//                .in("age", Stream.of(20,24,26).collect(Collectors.toList()))
//                .and(new ConditionEntity<>(Employee.class).like("dept.name", "财务"));

        Employee employee = new Employee();
        employee.setEmpName("张三");
        employee.setAge(26);
        int id = customDao.
//        int i = customDao.deleteByKey(Dept.class, 13);


    }
}
