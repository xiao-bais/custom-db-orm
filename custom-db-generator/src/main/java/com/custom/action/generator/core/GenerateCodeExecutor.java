package com.custom.action.generator.core;

import com.custom.action.dbaction.AbstractSqlExecutor;
import com.custom.action.generator.config.GlobalConfig;
import com.custom.action.generator.config.PackageConfig;
import com.custom.action.generator.config.TableConfig;
import com.custom.action.generator.table.ColumnStructModel;
import com.custom.action.sqlparser.JdbcAction;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.comm.SymbolConst;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2022/4/19 11:54
 * @Desc：
 **/
public class GenerateCodeExecutor {

    private static final Logger logger = LoggerFactory.getLogger(GenerateCodeExecutor.class);

    private final static Map<String, Object> currCache = new ConcurrentHashMap<>();
    private static String DATA_BASE;
    private AbstractSqlExecutor sqlExecutor;

    public void start() {
        if (Objects.isNull(dbDataSource)) {
            ExThrowsUtil.toCustom("未配置数据源");
        }
        DATA_BASE = CustomUtil.getDataBase(dbDataSource.getUrl());
        if(Objects.isNull(dbCustomStrategy)) {
            dbCustomStrategy = new DbCustomStrategy();
        }
        AbstractSqlExecutor sqlExecutor = new JdbcAction(dbDataSource, dbCustomStrategy);

        if(JudgeUtilsAx.isEmpty(tables)) {
            ExThrowsUtil.toCustom("未指定表名");
        }

    }

    public List<Map<String, Map<String, Object>>> tableStructs() {

        List<String> truthTables = getTruthTables();
        if(truthTables.isEmpty()) {
            ExThrowsUtil.toCustom("表名皆不存在");
        }
        String tableStr = Arrays.stream(tables)
                .filter(CustomUtil::isNotBlank)
                .map(x -> String.format("'%s'", x))
                .collect(Collectors.joining(","));
        String selectTableSql = CustomUtil.loadFiles("/queryTableStruct.sql");
        sqlExecutor.executeQueryNotPrintSql(ColumnStructModel.class, selectTableSql, )




        return null;
    }

    /**
     * 获取真实表名
     */
    private List<String> getTruthTables() {
        List<String> truthTables = new ArrayList<>();
        try {
            String tableStr = Arrays.stream(tables)
                    .filter(CustomUtil::isNotBlank)
                    .map(x -> String.format("'%s'", x))
                    .collect(Collectors.joining(","));

            String selectTableSql = String.format("SELECT TABLE_NAME COUNT FROM `information_schema`.`TABLES` WHERE TABLE_NAME in (%s) " +
                    "AND TABLE_SCHEMA = '%s'", tableStr, DATA_BASE);
            truthTables = sqlExecutor.executeQueryNotPrintSql(String.class, selectTableSql);
        }catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return truthTables;
    }





    /**
     * 数据源
     */
    private DbDataSource dbDataSource;

    /**
     * 策略配置
     */
    private DbCustomStrategy dbCustomStrategy;

    /**
     * 全局配置
     */
    private GlobalConfig globalConfig;

    /**
     * 包的配置
     */
    private PackageConfig packageConfig;

    /**
     * 表与实体的配置
     */
    private TableConfig tableConfig;

    /**
     * 需要生成的表
     */
    private String[] tables;

    public DbDataSource getDbDataSource() {
        return dbDataSource;
    }

    public void setDbDataSource(DbDataSource dbDataSource) {
        this.dbDataSource = dbDataSource;
    }

    public DbCustomStrategy getDbCustomStrategy() {
        return dbCustomStrategy;
    }

    public void setDbCustomStrategy(DbCustomStrategy dbCustomStrategy) {
        this.dbCustomStrategy = dbCustomStrategy;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public PackageConfig getPackageConfig() {
        return packageConfig;
    }

    public void setPackageConfig(PackageConfig packageConfig) {
        this.packageConfig = packageConfig;
    }

    public TableConfig getTableConfig() {
        return tableConfig;
    }

    public void setTableConfig(TableConfig tableConfig) {
        this.tableConfig = tableConfig;
    }

    public String[] getTables() {
        return tables;
    }

    public void setTables(String[] tables) {
        this.tables = tables;
    }
}
