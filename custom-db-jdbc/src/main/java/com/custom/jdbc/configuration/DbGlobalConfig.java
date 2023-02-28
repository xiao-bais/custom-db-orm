package com.custom.jdbc.configuration;

import com.custom.comm.enums.TableNameStrategy;
import com.custom.jdbc.executor.CustomSqlExecuteBefore;
import com.custom.jdbc.executor.CustomSqlQueryAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Xiao-Bai
 * @since 2023/2/21 12:19
 * 全局配置
 */
@Component
@ConfigurationProperties(prefix = "custom.db.config")
public class DbGlobalConfig {

    /**
     * 表前缀，在DbTable注解未填写value()的情况下
     * 由该tableNamePrefix + 实体类名组成表名(具体配置规则，可按照tableNameStrategy)
     */
    private String tableNamePrefix = "";

    /**
     * 表名前缀拼接策略
     */
    private TableNameStrategy tableNameStrategy = TableNameStrategy.DEFAULT;

    /**
     * sql执行查询后的处理类，若实现类加入spring容器则无需再在此配置，两者选其一即可
     */
    private Class<? extends CustomSqlQueryAfter> sqlQueryAfter;

    /**
     * sql执行前的拦截处理类，若实现类加入spring容器则无需再在此配置，两者选其一即可
     */
    private Class<? extends CustomSqlExecuteBefore> sqlInterceptor;

    /**
     * 自定义的策略配置
     */
    private DbCustomStrategy strategy;


    public String getTableNamePrefix() {
        return tableNamePrefix;
    }

    public void setTableNamePrefix(String tableNamePrefix) {
        this.tableNamePrefix = tableNamePrefix;
    }

    public TableNameStrategy getTableNameStrategy() {
        return tableNameStrategy;
    }

    public void setTableNameStrategy(TableNameStrategy tableNameStrategy) {
        this.tableNameStrategy = tableNameStrategy;
    }

    public Class<? extends CustomSqlQueryAfter> getSqlQueryAfter() {
        return sqlQueryAfter;
    }

    public void setSqlQueryAfter(Class<? extends CustomSqlQueryAfter> sqlQueryAfter) {
        this.sqlQueryAfter = sqlQueryAfter;
    }

    public Class<? extends CustomSqlExecuteBefore> getSqlInterceptor() {
        return sqlInterceptor;
    }

    public void setSqlInterceptor(Class<? extends CustomSqlExecuteBefore> sqlInterceptor) {
        this.sqlInterceptor = sqlInterceptor;
    }

    public DbCustomStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(DbCustomStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * 默认配置
     */
    public static DbGlobalConfig defaultConfig() {
        DbGlobalConfig globalConfig = new DbGlobalConfig();
        globalConfig.setStrategy(new DbCustomStrategy());
        return globalConfig;
    }
}
