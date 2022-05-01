package com.home.customtest;

import com.alibaba.fastjson.JSON;
import com.custom.action.sqlproxy.ReaderExecutorProxy;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.wrapper.Conditions;
import com.custom.comm.CustomUtil;
import com.custom.comm.page.DbPageRows;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.home.customtest.dao.CustomTestDao;
import com.home.customtest.entity.Student;

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
//        DbDataSource dbDataSource = new DbDataSource();
//        dbDataSource.setUrl("jdbc:mysql://39.108.225.176:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC");
//        dbDataSource.setUsername("root");
//        dbDataSource.setPassword("xh@Mysql1524");
//
//        // 增删改查映射策略配置
//        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
//        dbCustomStrategy.setSqlOutPrinting(true);
////        dbCustomStrategy.setSqlOutPrintExecute(true);
//        dbCustomStrategy.setUnderlineToCamel(true);
//        dbCustomStrategy.setDbFieldDeleteLogic("state");
//        dbCustomStrategy.setDeleteLogicValue(1);
//        dbCustomStrategy.setNotDeleteLogicValue(0);
//
//        JdbcDao jdbcDao = new JdbcDao(dbDataSource, dbCustomStrategy);
//        TableInfoCache.setUnderlineToCamel(true);
//
//        List<Student> students = jdbcDao.selectList(Conditions.lambdaQuery(Student.class)
//                .eq(Student::getName, "张重阳")
//                .onlyPrimary()
//        );
//        System.out.println("students = " + students);

        Object a = 0.3;
        Object b = " nsada";

        System.out.println("a = " + CustomUtil.isBasicType(a));
        System.out.println("b = " + CustomUtil.isBasicType(b));


    }


}
