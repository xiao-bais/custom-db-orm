package com.home.customtest;

import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.handler.JdbcDao;
import com.custom.proxy.SqlReaderExecuteProxy;
import com.custom.sqlparser.CustomDao;
import com.home.customtest.dao.CustomTestDao;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.WorkEmp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
public class DoMain {


    public static void main(String[] args) throws Exception {

        String regex = "[@]\\w+[@]";
        String sql = "aaaa @vvv@ puji";
        Matcher matcher = Pattern.compile(regex).matcher(sql);
        System.out.println("matcher.matches() = " + matcher.matches());


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




        DbDataSource dbDataSource = new DbDataSource();
        dbDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("123456");

        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
//        dbCustomStrategy.setSqlOutPrinting(true);
//        dbCustomStrategy.setUnderlineToCamel(true);
        dbCustomStrategy.setDbFieldDeleteLogic("state");
        dbCustomStrategy.setDeleteLogicValue("1");
        dbCustomStrategy.setNotDeleteLogicValue("0");

        // 以动态代理的方式来执行dao层接口的方法。类似于mybatis
//        CustomTestDao customTestDao = new SqlReaderExecuteProxy(dbDataSource, dbCustomStrategy).createProxy(CustomTestDao.class);
//
//        WorkEmp workEmp = new WorkEmp();
//
//        workEmp.setAgeList(Arrays.asList(21,23,24));
//        workEmp.setEmpName("员工-3");
//        workEmp.setAge(55);
//        for (int i = 0; i < 10; i++) {
//            List<Employee> conditr = customTestDao.getConditr(workEmp);
//        }

//        System.out.println("conditr = " + conditr);

//        String oneByCond = customTestDao.selectOneByCond(1, 21, "age");
//        System.out.println("oneByCond = " + oneByCond);
//
//        Employee employee = customTestDao.selectByOne(21);
//        System.out.println("employee = " + employee);





        // JdbcDao/CustomDao 两个dao的功能几乎一模一样 不同的在于注解的解析方式
//        JdbcDao jdbcDao = new JdbcDao(dbDataSource, dbCustomStrategy);
//        CustomDao customDao = new CustomDao(dbDataSource, dbCustomStrategy);
//
//        Employee employee1 = new Employee();
//        employee1.setAddress("混哪呢");
//        employee1.setId(2);
//
//        customDao.updateByKey(employee1);
//
//
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
//
//        // 插入多条记录
//        jdbcDao.insert(list);
//
//
//        // 一般查询
//        List<Employee> list1 = jdbcDao.selectList(Employee.class, " and a.age > 20");
//        System.out.println("list1 = " + list1);


    }
}
