package com.custom.jdbc;

import com.custom.jdbc.configuretion.DbCustomStrategy;
import com.custom.jdbc.configuretion.DbDataSource;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/17 2:54
 * @Desc 全局配置对象
 */
public class CustomConfigHelper {

    private DbDataSource dbDataSource;

    private DbCustomStrategy dbCustomStrategy;

    public CustomConfigHelper(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        this.dbDataSource = dbDataSource;
        this.dbCustomStrategy = dbCustomStrategy;
    }

    public CustomConfigHelper() {
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
}
