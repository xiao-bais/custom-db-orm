package com.home.customtest;

import com.custom.sqlparser.CustomEntityCacheBuilder;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.sqlparser.CustomDao;
import com.custom.wrapper.ConditionEntity;
import com.home.customtest.entity.Employee;

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

        CustomDao customDao1 = new CustomDao(dbDataSource, dbCustomStrategy);


        List<Employee> employees1 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Employee employee = new Employee();
            employee.setEmpName("里欧iin" + i);
            employee.setAddress("换带坏大111" + i);
            employee.setAge(23);
            employee.setSex(false);
            employee.setBirthday(new Date());
            employee.setDeptId(1);
            employee.setAreaId(2);
            employees1.add(employee);
        }

        long time = System.currentTimeMillis();

        long insert = customDao1.insert(employees1);

        long time1 = System.currentTimeMillis();

        System.out.println("time = " + (time1 - time));


        dbCustomStrategy.setEntityScans(new String[]{"com.home.customtest.entity"});
        CustomEntityCacheBuilder builder = new CustomEntityCacheBuilder(dbCustomStrategy);
        builder.buildEntity();

        CustomDao customDao2 = new CustomDao(dbDataSource, dbCustomStrategy);

        long time2 = System.currentTimeMillis();

        long insert1 = customDao2.insert(employees1);

        long time3 = System.currentTimeMillis();

        System.out.println("time2 = " + (time3 - time2));

//        ConditionEntity<Employee> conditionEntity = new ConditionEntity<>(Employee.class);
//        conditionEntity.like("emp_name", "工")
//                .select("emp_name", "age", "name", "dept.name")
//                .eq("sex", true)
//                .in("age", Stream.of(20, 24, 26).collect(Collectors.toList()))
//                .and(new ConditionEntity<>(Employee.class).like("dept.name", "财务"));


//        List<Employee> employees = customDao.selectList(Employee.class, conditionEntity);
//        System.out.println("employees = " + employees);


    }


}
