package com.home.customtest;

import com.custom.action.wrapper.Conditions;
import com.custom.action.wrapper.LambdaConditionEntity;
import com.custom.generator.config.GlobalConfig;
import com.custom.generator.config.PackageConfig;
import com.custom.generator.config.TableConfig;
import com.custom.generator.core.GenerateCodeExecutor;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.comm.enums.KeyStrategy;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.custom.generator.FreemarkerTemplateStructs;
import com.custom.generator.model.TableStructModel;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Student;

import java.util.Arrays;
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


        LambdaConditionEntity<ChildStudent> lambdaQuery = Conditions.lambdaQuery(ChildStudent.class);
        String[] selectColumns = lambdaQuery.select(ChildStudent::getNickName).getSelectColumns();
//        System.out.println("selectColumns = " + Arrays.toString(selectColumns));
//        List<Object> objects = jdbcDao.selectObjs(lambdaQuery.eq());
//        System.out.println("objects = " + objects);


    }



}
