package com.home.customtest;

import com.custom.sqlparser.CustomEntityCacheBuilder;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.sqlparser.CustomDao;
import com.custom.wrapper.ColumnParseHandler;
import com.custom.wrapper.ConditionEntity;
import com.custom.wrapper.LambdaConditionEntity;
import com.home.customtest.entity.Employee;
import com.home.customtest.fun.MyFun;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
//        DbDataSource dbDataSource = new DbDataSource();
//        dbDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC");
//        dbDataSource.setUsername("root");
//        dbDataSource.setPassword("123456");
//
//        // 增删改查映射策略配置
//        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
//        dbCustomStrategy.setSqlOutPrinting(true);
//        dbCustomStrategy.setSqlOutUpdate(true);
//        dbCustomStrategy.setUnderlineToCamel(true);
//        dbCustomStrategy.setDbFieldDeleteLogic("state");
//        dbCustomStrategy.setDeleteLogicValue("1");
//        dbCustomStrategy.setNotDeleteLogicValue("0");
//
//        CustomDao customDao1 = new CustomDao(dbDataSource, dbCustomStrategy);

        Employee employee = new Employee();
        employee.setSex(false);

        LambdaConditionEntity<Employee> condition = new LambdaConditionEntity<>(Employee.class);
        condition.eq(Employee::getEmpName, "张三").le(Employee::getAge, 22);


        ConditionEntity<Employee> conditionEntity = new ConditionEntity<>(Employee.class);
        conditionEntity.eq("emp_name", "张三").le("age", 22);



    }


}
