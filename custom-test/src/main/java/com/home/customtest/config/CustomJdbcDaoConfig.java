package com.home.customtest.config;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Xiao-Bai
 * @date 2022/5/7 22:17
 * @desc:
 */
//@Configuration
public class CustomJdbcDaoConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DbDataSource dbDataSource() {
        return new DbDataSource();
    }

    @Bean
    @ConfigurationProperties(prefix = "custom.db.strategy")
    public DbCustomStrategy dbCustomStrategy() {
        return new DbCustomStrategy();
    }

    @Bean
    public JdbcDao jdbcDao() {
        DbDataSource dbDataSource = dbDataSource();
        DbCustomStrategy dbCustomStrategy = dbCustomStrategy();
        JdbcDao jdbcDao = new JdbcDao(dbDataSource, dbCustomStrategy);
        System.out.println("aaa");
        return jdbcDao;
    }


}
