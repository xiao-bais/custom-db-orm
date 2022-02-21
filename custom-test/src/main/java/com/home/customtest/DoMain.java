package com.home.customtest;

import com.custom.comm.page.DbPageRows;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.sqlparser.CustomDao;
import com.custom.wrapper.ConditionEntity;
import com.home.customtest.entity.Dept;
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

        // 增删改查映射策略配置
        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
        dbCustomStrategy.setSqlOutPrinting(true);
        dbCustomStrategy.setUnderlineToCamel(true);
//        dbCustomStrategy.setDbFieldDeleteLogic("state");
//        dbCustomStrategy.setDeleteLogicValue("1");
//        dbCustomStrategy.setNotDeleteLogicValue("0");

        long time = System.currentTimeMillis();
        CustomDao customDao = new CustomDao(dbDataSource, dbCustomStrategy);
        ConditionEntity<Employee> conditionEntity = new ConditionEntity<>(Employee.class);
        conditionEntity.like("emp_name", "工")
                .eq("sex", false)
                .in("age", Arrays.asList(20,25,26))
//                .or(new ConditionEntity<>(Employee.class).like("dept.name", "财务"))
                .setEnabledRelatedCondition(true);
        long time1 = System.currentTimeMillis();
        System.out.println("time = " + (time1-time));

        DbPageRows<Employee> employeeDbPageRows1 = customDao.selectPageRows(Employee.class, null, conditionEntity);
        long time2 = System.currentTimeMillis();
        DbPageRows<Employee> employeeDbPageRows2 = customDao.selectPageRows(Employee.class, null, conditionEntity);
        long time3 = System.currentTimeMillis();
        DbPageRows<Employee> employeeDbPageRows3 = customDao.selectPageRows(Employee.class, null, conditionEntity);
        long time4 = System.currentTimeMillis();
        DbPageRows<Employee> employeeDbPageRows4 = customDao.selectPageRows(Employee.class, null, conditionEntity);
        long time5 = System.currentTimeMillis();
        System.out.println("time1 = " + (time2-time1));
        System.out.println("time2 = " + (time3-time1));
        System.out.println("time3 = " + (time4-time1));
        System.out.println("time4 = " + (time5-time1));

    }
}
