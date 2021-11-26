package com.custom.dbconfig;

import com.custom.comm.CustomUtil;
import com.custom.handler.JdbcDao;
import com.custom.handler.proxy.SqlReaderExecuteProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Author Xiao-Bai
 * @Date 2021/11/23 17:47
 * @Desc：
 **/
@Configuration
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
        if(!CustomUtil.isDataSourceEmpty(dbDataSource)) {
            logger.info("SqlReaderExecuteProxy Initialized Successfully !");
            return new SqlReaderExecuteProxy(dbDataSource, dbCustomStrategy);
        }
        return null;
    }

    @Bean
    @ConditionalOnBean(DbDataSource.class)
    public JdbcDao jdbcDao(){
        if(!CustomUtil.isDataSourceEmpty(dbDataSource)) {
            logger.info("JdbcDao Initialized Successfully !");
            return new JdbcDao(dbDataSource, dbCustomStrategy);
        }
        return null;
    }



}
