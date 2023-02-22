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
     * 全局配置
     */
    private DbGlobalConfig dbGlobalConfig;

    /**
     * 数据库适配对象
     */
    private final DatabaseAdapter databaseAdapter;


    public CustomConfigHelper(DbDataSource dbDataSource, DbGlobalConfig dbGlobalConfig, DatabaseAdapter databaseAdapter) {
        this.dbDataSource = dbDataSource;
        this.dbGlobalConfig = dbGlobalConfig;
        this.databaseAdapter = databaseAdapter;
    }

    public DbDataSource getDbDataSource() {
        return dbDataSource;
    }

    public void setDbDataSource(DbDataSource dbDataSource) {
        this.dbDataSource = dbDataSource;
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
