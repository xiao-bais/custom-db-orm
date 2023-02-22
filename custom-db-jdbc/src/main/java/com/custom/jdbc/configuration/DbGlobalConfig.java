package com.custom.jdbc.configuration;

import com.custom.jdbc.executor.CustomSqlInterceptor;
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
     * sql执行查询后的处理类
     */
    private Class<? extends CustomSqlQueryAfter> sqlQueryAfter;

    /**
     * sql执行前的拦截处理类
     */
    private Class<? extends CustomSqlInterceptor> sqlInterceptor;


    /**
     * 自定义的策略配置
     */
    private DbCustomStrategy strategy;



    public Class<? extends CustomSqlQueryAfter> getSqlQueryAfter() {
        return sqlQueryAfter;
    }

    public void setSqlQueryAfter(Class<? extends CustomSqlQueryAfter> sqlQueryAfter) {
        this.sqlQueryAfter = sqlQueryAfter;
    }

    public Class<? extends CustomSqlInterceptor> getSqlInterceptor() {
        return sqlInterceptor;
    }

    public void setSqlInterceptor(Class<? extends CustomSqlInterceptor> sqlInterceptor) {
        this.sqlInterceptor = sqlInterceptor;
    }

    public DbCustomStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(DbCustomStrategy strategy) {
        this.strategy = strategy;
    }
}
