package com.home;

import com.custom.action.SqlConstants;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.wrapper.ConditionWrapper;
import com.custom.action.wrapper.Conditions;
import com.custom.comm.CustomUtil;
import com.custom.comm.RexUtil;
import com.custom.comm.page.DbPageRows;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.custom.proxy.InterfacesProxyExecutor;
import com.home.customtest.dao.CustomTestDao;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;
import com.home.customtest.entity.WorkEmp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

//        Map<String, String> data = new HashMap<>();
//        data.put("name", "zhangs");
//        data.put("code", "2222");
//        String content = "恭喜${name}报名成功，请凭报名编号${code}到现场参加活动";
//        String pattern = "\\$\\{(.+?)\\}";
//        Pattern p = Pattern.compile(pattern);
//        Matcher m = p.matcher(content);
//        StringBuffer sb = new StringBuffer();
//        while (m.find())
//        {
//            String key = m.group(1);
//            String value = data.get(key);
//            m.appendReplacement(sb, value == null ? "" : value);
//        }
//        m.appendTail(sb);
//        System.out.println(sb.toString());






    }


}
