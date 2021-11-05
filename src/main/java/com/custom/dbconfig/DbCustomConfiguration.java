//package com.custom.dbconfig;
//
//import com.custom.handler.JdbcDao;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author Xiao-Bai
// * @date 2021/11/4 19:11
// * @desc:
// */
//@Configuration
//public class DbCustomConfiguration {
//
//    @Value(value = "#{${custom.db.datasource}}")
//    private DbDataSource dbDataSource;
//
//    @Value(value = "#{${custom.db.strategy}}")
//    private DbCustomStrategy dbCustomStrategy;
//
//    @Bean
//    @ConfigurationProperties(prefix = "custom.db.datasource")
//    public DbCustomStrategy dbCustomStrategy() {
//        return this.dbCustomStrategy;
//    }
//
//    @Bean
//    public DbDataSource dbDataSource() {
//        this.dbDataSource.setDbCustomStrategy(dbCustomStrategy);
//        return this.dbDataSource;
//    }
//
//    @Bean(name = "jdbcDao")
//    public JdbcDao jdbcDao() {
//        return new JdbcDao(this.dbDataSource);
//    }
//
//
//}
