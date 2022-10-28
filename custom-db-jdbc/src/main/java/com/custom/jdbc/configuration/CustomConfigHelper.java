package com.custom.jdbc.configuration;

import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.interfaces.DatabaseAdapter;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/17 2:54
 * @Desc 全局配置对象
 */
public class CustomConfigHelper {

    private DbDataSource dbDataSource;

    private DbCustomStrategy dbCustomStrategy;

    private DatabaseAdapter databaseAdapter;

    public CustomConfigHelper(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        this.dbDataSource = dbDataSource;
        this.dbCustomStrategy = dbCustomStrategy;
    }

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
