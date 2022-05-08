package com.home.customtest;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.wrapper.Conditions;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.home.customtest.entity.ChildStudent;

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

        JdbcDao jdbcDao = new JdbcDao(dbDataSource, dbCustomStrategy);
        TableInfoCache.setUnderlineToCamel(true);

        List<ChildStudent> childStudents = jdbcDao.selectList(Conditions.lambdaQuery(ChildStudent.class)
                .eq(ChildStudent::getName, "张三11d,a21lss")
                .between(ChildStudent::getAge, 20, 25)
                .select(ChildStudent::getAge)
                .select(x -> x.sum(ChildStudent::getAge, ChildStudent::getSumAge))
                .groupBy(ChildStudent::getAge)
        );


    }


}
