package com.custom.springboot;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.comm.JudgeUtil;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.custom.proxy.InterfacesProxyExecutor;
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
    public InterfacesProxyExecutor readerExecutorProxy() {
        if(isDataSourceEmpty(dbDataSource)) {
            return null;
        }
        logger.info("InterfacesProxyExecutor Initialized Successfully !");
        return new InterfacesProxyExecutor(dbDataSource, dbCustomStrategy);
    }

    @Bean
    @Primary
    @ConditionalOnBean(DbDataSource.class)
    public JdbcDao jdbcDao(){
        if(isDataSourceEmpty(dbDataSource)) {
            return null;
        }
        TableInfoCache.setUnderlineToCamel(dbCustomStrategy.isUnderlineToCamel());
        JdbcDao jdbcDao = new JdbcDao(dbDataSource, dbCustomStrategy);
        logger.info("JdbcDao Initialized Successfully !");
        return jdbcDao;
    }

    private boolean isDataSourceEmpty(DbDataSource dbDataSource) {
        return JudgeUtil.isEmpty(dbDataSource.getUrl()) || JudgeUtil.isEmpty(dbDataSource.getUsername()) || JudgeUtil.isEmpty(dbDataSource.getPassword());
    }


}
