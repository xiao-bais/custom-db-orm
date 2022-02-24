package com.custom.dbconfig;

import com.custom.comm.CustomUtil;
import com.custom.handler.JdbcDao;
import com.custom.proxy.SqlReaderExecuteProxy;
import com.custom.sqlparser.CustomDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * @Author Xiao-Bai
 * @Date 2021/11/23 17:47
 * @Desc：
 **/
@Configuration
@ComponentScan("com.custom")
public class CustomConfiguration {

    private static Logger logger = LoggerFactory.getLogger(CustomConfiguration.class);

    private DbDataSource dbDataSource;

    private DbCustomStrategy dbCustomStrategy;

    public CustomConfiguration(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        this.dbDataSource = dbDataSource;
        this.dbCustomStrategy = dbCustomStrategy;
    }

    @Bean
    @ConditionalOnBean(DbDataSource.class)
    public SqlReaderExecuteProxy sqlReaderExecuteProxy() {
        if(CustomUtil.isDataSourceEmpty(dbDataSource)) {
            return null;
        }
        logger.info("SqlReaderExecuteProxy Initialized Successfully !");
        return new SqlReaderExecuteProxy(dbDataSource, dbCustomStrategy);
    }

    @Bean
    @ConditionalOnBean(DbDataSource.class)
    public JdbcDao jdbcDao(){
        if(CustomUtil.isDataSourceEmpty(dbDataSource)) {
            return null;
        }
        logger.info("JdbcDao Initialized Successfully !");
        return new JdbcDao(dbDataSource, dbCustomStrategy);
    }

    @Bean
    @ConditionalOnBean(DbDataSource.class)
    public CustomDao customDao(){
        if(CustomUtil.isDataSourceEmpty(dbDataSource)) {
            return null;
        }
        logger.info("CustomDao Initialized Successfully !");
        return new CustomDao(dbDataSource, dbCustomStrategy);
    }



}
