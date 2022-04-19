package com.home.customtest;

import com.custom.action.fieldfill.AutoFillColumnHandler;
import com.custom.action.fieldfill.TableFillObject;
import com.custom.action.generator.config.TableConfig;
import com.custom.action.generator.core.GenerateCodeExecutor;
import com.custom.action.generator.table.TableStructModel;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.wrapper.Conditions;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.home.customtest.config.CustomFillConfig;
import com.home.customtest.entity.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        dbDataSource.setUrl("jdbc:mysql://39.108.225.176:3306/shop?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC");
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


        GenerateCodeExecutor gce = new GenerateCodeExecutor(dbDataSource, dbCustomStrategy);

        // 表配置
        TableConfig tableConfig = new TableConfig();
        tableConfig.setTablePrefix("shop");
        tableConfig.setEntitySuffix("PO");
        tableConfig.setEntityDbFieldAnnotationValueEnable(true);
        gce.setTableConfig(tableConfig);

        TableStructModel tableStructModel = new TableStructModel();
        tableStructModel.setTable("shop_user");

        gce.start();

        System.out.println("tableStructModel.getEntityName() = " + tableStructModel.getEntityName());


    }



}
