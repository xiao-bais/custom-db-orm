package com.custom.jdbc.configuration;

import com.custom.jdbc.interfaces.DatabaseAdapter;

/**
 * 全局配置对象
 * @author  Xiao-Bai
 * @since  2022/7/17 2:54
 */
public class CustomConfigHelper {

    /**
     * 数据源
     */
    private DbDataSource dbDataSource;

    /**
     * 自定义策略
     */
    private DbCustomStrategy dbCustomStrategy;

    /**
     * 全局配置
     */
    private DbGlobalConfig dbGlobalConfig;

    /**
     * 数据库适配对象
     */
    private final DatabaseAdapter databaseAdapter;


    public CustomConfigHelper(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy, DatabaseAdapter databaseAdapter) {
        this.dbDataSource = dbDataSource;
        this.dbCustomStrategy = dbCustomStrategy;
        this.databaseAdapter = databaseAdapter;
    }

    public CustomConfigHelper(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy, DatabaseAdapter databaseAdapter, DbGlobalConfig dbGlobalConfig) {
        this.dbDataSource = dbDataSource;
        this.dbCustomStrategy = dbCustomStrategy;
        this.databaseAdapter = databaseAdapter;
        this.dbGlobalConfig = dbGlobalConfig;
    }

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

    public DatabaseAdapter getDatabaseAdapter() {
        return databaseAdapter;
    }

    public DbGlobalConfig getDbGlobalConfig() {
        return dbGlobalConfig;
    }

    public void setDbGlobalConfig(DbGlobalConfig dbGlobalConfig) {
        this.dbGlobalConfig = dbGlobalConfig;
    }
}
