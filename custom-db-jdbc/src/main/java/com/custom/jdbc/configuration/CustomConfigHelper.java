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
     * 数据库适配对象
     */
    private DatabaseAdapter databaseAdapter;

    public CustomConfigHelper(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy, DatabaseAdapter databaseAdapter) {
        this.dbDataSource = dbDataSource;
        this.dbCustomStrategy = dbCustomStrategy;
        this.databaseAdapter = databaseAdapter;
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

    public void setDatabaseAdapter(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }
}
