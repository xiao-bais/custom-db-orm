package com.custom.dbconfig;

import com.custom.comm.CustomUtil;
import com.custom.proxy.SqlReaderExecuteProxy;
import com.custom.sqlparser.JdbcDao;
import com.custom.sqlparser.TableInfoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;


/**
 * @Author Xiao-Bai
 * @Date 2021/11/23 17:47
 * @Desc：
 **/
@Configuration
@Order(8)
@ComponentScan("com.custom")
public class CustomConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CustomConfiguration.class);

    private final DbDataSource dbDataSource;
    private final DbCustomStrategy dbCustomStrategy;

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
    @Primary
    @ConditionalOnBean(DbDataSource.class)
    public JdbcDao jdbcDao(){
        if(CustomUtil.isDataSourceEmpty(dbDataSource)) {
            return null;
        }
        TableInfoCache.setUnderlineToCamel(dbCustomStrategy.isUnderlineToCamel());
        JdbcDao jdbcDao = new JdbcDao(dbDataSource, dbCustomStrategy);
        logger.info("CustomDao Initialized Successfully !");
        return jdbcDao;
    }


}
