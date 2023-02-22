package com.custom.jdbc.configuration;

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
     * sql执行<b>查询</b>后的处理类
     * <br/> 填入接口{@link com.custom.jdbc.executor.CustomSqlQueryAfter}的实现类
     * <br/> 例如: com.example.MyCustomSqlQueryAfter
     * <br/> 若该实现类加入了spring容器，则无需再配置文件中配置该值，两者选其一即可
     * @see com.custom.jdbc.executor.CustomSqlQueryAfter
     */
    private String sqlQueryAfter;

    /**
     * sql执行前的拦截处理类
     * <br/> 填入接口{@link com.custom.jdbc.executor.CustomSqlInterceptor}的实现类
     * <br/> 例如: com.example.MyCustomSqlInterceptor
     * <br/> 若该实现类加入了spring容器，则无需再配置文件中配置该值，两者选其一即可
     * @see com.custom.jdbc.executor.CustomSqlInterceptor
     */
    private String sqlInterceptor;


    /**
     * 自定义的策略配置
     */
    private DbCustomStrategy strategy;


    public String getSqlQueryAfter() {
        return sqlQueryAfter;
    }

    public void setSqlQueryAfter(String sqlQueryAfter) {
        this.sqlQueryAfter = sqlQueryAfter;
    }

    public String getSqlInterceptor() {
        return sqlInterceptor;
    }

    public void setSqlInterceptor(String sqlInterceptor) {
        this.sqlInterceptor = sqlInterceptor;
    }

    public DbCustomStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(DbCustomStrategy strategy) {
        this.strategy = strategy;
    }
}
