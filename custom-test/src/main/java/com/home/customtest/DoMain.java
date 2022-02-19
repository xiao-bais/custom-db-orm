package com.home.customtest;

import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.sqlparser.CustomDao;
import com.custom.wrapper.ConditionEntity;
import com.home.customtest.entity.Employee;

import java.util.Arrays;
import java.util.List;

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

        // 数据库策略配置
        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
        dbCustomStrategy.setSqlOutPrinting(true);
        dbCustomStrategy.setUnderlineToCamel(true);
//        dbCustomStrategy.setDbFieldDeleteLogic("state");
//        dbCustomStrategy.setDeleteLogicValue("1");
//        dbCustomStrategy.setNotDeleteLogicValue("0");

        CustomDao customDao = new CustomDao(dbDataSource, dbCustomStrategy);

        ConditionEntity<Employee> conditionEntity = new ConditionEntity<>(Employee.class);
        conditionEntity.like("emp_name", "工").eq("sex", false).in("age", Arrays.asList(20,25,26));
        String selectSql = conditionEntity.getFinalConditional();
        System.out.println("selectSql = " + selectSql);

        List<Employee> employees = customDao.selectList(Employee.class, conditionEntity);
        System.out.println("employees = " + employees);
//        System.out.println("list = " + list);

    }
}
