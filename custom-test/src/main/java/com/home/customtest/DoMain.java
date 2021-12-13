package com.home.customtest;

import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.enums.DbSymbol;
import com.custom.enums.ExecuteMethod;
import com.custom.handler.DbParserFieldHandler;
import com.custom.handler.JdbcDao;
import com.custom.sqlparser.CustomDao;
import com.custom.sqlparser.TableSqlBuilder;
import com.custom.wrapper.ConditionEntity;
import com.home.customtest.entity.Employee;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
public class DoMain {


    public static void main(String[] args) throws Exception {


            ConditionEntity<Employee> entity = new ConditionEntity<>();

            Type type = ((ParameterizedType) entity.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//            int a = 15, b = 27;
//            ConditionEntity<Employee> conditionEntity = objectConditionEntity
//                    .where(DbSymbol.GREATER_THAN, true, "addr", "1232", null, null)
//                    .where(DbSymbol.BETWEEN, true, "age", a, b, null)
//                    .where(DbSymbol.LESS_THAN, true, "age", 30, null, null)
//                    .where(DbSymbol.EQUALS, true, "address", "张接不到", null, null)
//                    .where(DbSymbol.LIKE, true, "name", "k", null, "'%?%'")
//                    .where(DbSymbol.ORDER_BY, true, null, "id desc, name asc", null, null);
//
//            String and = objectConditionEntity.and(conditionEntity.toString());
//            System.out.println("and = " + and);




//        DbDataSource dbDataSource = new DbDataSource();
//        dbDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC");
//        dbDataSource.setUsername("root");
//        dbDataSource.setPassword("123456");
//
//        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
//        dbCustomStrategy.setSqlOutPrinting(true);
//        dbCustomStrategy.setDbFieldDeleteLogic("state");
//        dbCustomStrategy.setDeleteLogicValue("1");
//        dbCustomStrategy.setNotDeleteLogicValue("0");

//        JdbcDao jdbcDao = new JdbcDao(dbDataSource, dbCustomStrategy);
//        CustomDao customDao = new CustomDao(dbDataSource, dbCustomStrategy);

//        Employee employee = customDao.selectOneByKey(Employee.class, 2);
//        employee.setAddress("加利福尼亚");

//        Employee employee = new Employee();
//        employee.setAddress("混哪呢");
//        employee.setId(2);

//        customDao.updateByKey(employee);


//        List<Employee> list = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            Employee e = new Employee();
//            e.setEmpName("员-工bb-"+i);
//            e.setSex(i % 2 == 1);
//            e.setAddress("bbbb->" + i);
//            e.setAge(24-i);
//            e.setAreaId(i);
//            e.setDeptId(2);
//            e.setBirthday(new Date());
//            e.setState(0);
//            list.add(e);
//        }

//        jdbcDao.insert(list);

//        long time1 = System.currentTimeMillis();
//        TableSqlBuilder<Employee> tableSqlBuilder = new TableSqlBuilder<>(Employee.class, ExecuteMethod.UPDATE);
//        long time2 = System.currentTimeMillis();
//        String insertSql = tableSqlBuilder.getInsertSql();
//        long time3 = System.currentTimeMillis();
//        System.out.println("insertSql = " + insertSql);
//        List<Object> objValues = tableSqlBuilder.getManyObjValues();
//        long time4 = System.currentTimeMillis();
//        System.out.println("objValues = " + objValues);


//        dbCustomStrategy.setMapperScanEnable(true);
//        dbCustomStrategy.setPackageScans(new String[]{"com.custom.customtest.dao"});

//        JdbcDao jdbcDao = new JdbcDao(dbDataSource, dbCustomStrategy);
//        long time = System.currentTimeMillis();
//        TableSqlBuilder<Employee> tableSqlBuilder = new TableSqlBuilder<>(Employee.class, false);
//        long time1 = System.currentTimeMillis();
//
//        String selectSql = tableSqlBuilder.getSelectSql() + " where a.id = 1";
//        long time2 = System.currentTimeMillis();
//
//        DbParserFieldHandler dbParserFieldHandler = new DbParserFieldHandler();
//        String selectssql = dbParserFieldHandler.getSelectSql(Employee.class) + " where a.id = 3";
//        long time3 = System.currentTimeMillis();
//
//        Employee employee1 = jdbcDao.selectOneBySql(Employee.class, selectssql);
//        long time4 = System.currentTimeMillis();
//
//        Employee employee = jdbcDao.selectOneBySql(Employee.class, selectSql);
//        long time5 = System.currentTimeMillis();
//
//        System.out.println("time = " + (time1-time));
//        System.out.println("time = " + (time2-time1));
//        System.out.println("time = " + (time3-time2));
//        System.out.println("time = " + (time4-time3));
//        System.out.println("time = " + (time5-time4));
//
//        System.out.println("employee = " + employee);
//        System.out.println("employee1 = " + employee1);

    }
}
