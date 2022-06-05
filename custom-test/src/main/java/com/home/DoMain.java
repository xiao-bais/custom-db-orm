package com.home;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.comm.RexUtil;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.custom.proxy.InterfacesProxyExecutor;
import com.home.customtest.dao.CustomTestDao;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.WorkEmp;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
//        dbCustomStrategy.setSqlOutPrintExecute(true);
        dbCustomStrategy.setUnderlineToCamel(true);
        dbCustomStrategy.setDbFieldDeleteLogic("state");
        dbCustomStrategy.setDeleteLogicValue(1);
        dbCustomStrategy.setNotDeleteLogicValue(0);

        JdbcDao jdbcDao = new JdbcDao(dbDataSource, dbCustomStrategy);
        TableInfoCache.setUnderlineToCamel(true);

        InterfacesProxyExecutor proxyExecutor = new InterfacesProxyExecutor(dbDataSource, dbCustomStrategy);
        CustomTestDao customTestDao = proxyExecutor.createProxy(CustomTestDao.class);

        String sql = "select emp_name empName from employee where age in (#{emp.ageList}) and emp_name = #{emp.empName} and ${emp.id} = 0";

//        String allRex = RexUtil.replaceAllRex(sql, RexUtil.sql_rep_param, "emp.id", "aaa");
//        System.out.println("allRex = " + allRex);

        WorkEmp emp = new WorkEmp();
        emp.setAge(23);
        emp.setAgeList(Stream.of(21,22,26).collect(Collectors.toList()));
        emp.setEmpName("9jsa");
        emp.setId("minid");
        emp.getMap().put("admin", "admin123");
        emp.getMap().put("ads", 259);
        int[] arr = {21,22,23,24};
        List<Employee> employees = customTestDao.getConditr(emp);
//
        System.out.println("employees = " + employees);
//        List<Student> students = jdbcDao.selectList(Conditions.query(Student.class).gt("money", 3000.0).gt("age", 20));

    }

}
