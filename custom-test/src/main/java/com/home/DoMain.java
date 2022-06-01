package com.home;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.wrapper.Conditions;
import com.custom.action.wrapper.EmptyConditionWrapper;
import com.custom.action.wrapper.LambdaConditionWrapper;
import com.custom.action.wrapper.SelectFunc;
import com.custom.comm.page.DbPageRows;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Street;
import com.home.customtest.entity.Student;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
@SuppressWarnings("all")
public class DoMain {


    public static void main(String[] args) throws Exception {


        // 数据库连接配置
        DbDataSource dbDataSource = new DbDataSource();
        dbDataSource.setUrl("jdbc:mysql://39.108.225.176:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("xh@Mysql1524");

        // 增删改查映射策略配置
        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
        dbCustomStrategy.setSqlOutPrinting(true);
        dbCustomStrategy.setSqlOutPrintExecute(true);
        dbCustomStrategy.setUnderlineToCamel(true);
        dbCustomStrategy.setDbFieldDeleteLogic("state");
        dbCustomStrategy.setDeleteLogicValue(1);
        dbCustomStrategy.setNotDeleteLogicValue(0);

//        InterfacesProxyExecutor proxyExecutor = new InterfacesProxyExecutor(dbDataSource, dbCustomStrategy);
//        CustomTestDao customTestDao = proxyExecutor.createProxy(CustomTestDao.class);

//        System.out.println(CustomUtil.isBasicType(String.class));

//        WorkEmp emp = new WorkEmp();
//        emp.setAge(23);
//        emp.setAgeList(Stream.of(21,22,26).collect(Collectors.toList()));
//        emp.setEmpName(null);
//        emp.getMap().put("admin", "admin123");
//        emp.getMap().put("ads", 259);
//        List<Employee> employees = customTestDao.getConditr(emp);
//
//        System.out.println("employees = " + employees);

        JdbcDao jdbcDao = new JdbcDao(dbDataSource, dbCustomStrategy);
        TableInfoCache.setUnderlineToCamel(true);

        Integer[] strings = jdbcDao.selectArr(Integer.class, "select age from student where age > ?", 20);
        System.out.println("strings = " + Arrays.toString(strings));

    }

}
