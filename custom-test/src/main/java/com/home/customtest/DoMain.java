package com.home.customtest;

import com.custom.generator.config.GlobalConfig;
import com.custom.generator.config.PackageConfig;
import com.custom.generator.config.TableConfig;
import com.custom.generator.core.GenerateCodeExecutor;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.comm.enums.KeyStrategy;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.custom.generator.ftl.FreemarkerUtil;
import com.custom.generator.model.TableStructModel;

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


        // 包配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParentPackage("com.home.shop");
        packageConfig.setEntity("pojo");
        packageConfig.setService("service");
        packageConfig.setController("controller");
        gce.setPackageConfig(packageConfig);

        // 全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setAuthor("Xiao-Bai");
        globalConfig.setOutputDir("src/main/java");
        globalConfig.setKeyStrategy(KeyStrategy.AUTO);
//        globalConfig.setEntityLombok(true);
//        globalConfig.setSwagger(true);
//        globalConfig.set
        gce.setGlobalConfig(globalConfig);

        String[] tables = {"shop_cart", "shop_category", "shop_order", "shop_product", "shop_user"};
        gce.setTables(tables);

        gce.start();

        List<TableStructModel> tableStructModels = gce.getTableStructModels();
        for (TableStructModel tableStructModel : tableStructModels) {
            FreemarkerUtil.buildEntity(tableStructModel);
        }



    }



}
