package com.custom.springboot;

import com.custom.action.proxy.JdbcDaoProxy;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.comm.JudgeUtil;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.custom.proxy.InterfacesProxyExecutor;
import com.custom.springboot.scanner.RegisterBeanExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.*;


/**
 * @Author Xiao-Bai
 * @Date 2021/11/23 17:47
 * @Desc：
 **/
@Configuration
@ComponentScan("com.custom")
@Import({RegisterBeanExecutor.class})
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
    public JdbcOpDao jdbcOpDao(){
        if(isDataSourceEmpty(dbDataSource)) {
            return null;
        }
        TableInfoCache.setUnderlineToCamel(dbCustomStrategy.isUnderlineToCamel());
        JdbcOpDao jdbcDao = new JdbcOpDao(dbDataSource, dbCustomStrategy);
        logger.info("JdbcOpDao Initialized Successfully !");
        return jdbcDao;
    }

    @Bean
    @Primary
    @ConditionalOnBean(DbDataSource.class)
    public JdbcDao jdbcDao(){
        if(isDataSourceEmpty(dbDataSource)) {
            return null;
        }
        TableInfoCache.setUnderlineToCamel(dbCustomStrategy.isUnderlineToCamel());
        JdbcDao jdbcDao = new JdbcDaoProxy(dbDataSource, dbCustomStrategy).createProxy();
        logger.info("JdbcDao Initialized Successfully !");
        return jdbcDao;
    }

    private boolean isDataSourceEmpty(DbDataSource dbDataSource) {
        return JudgeUtil.isEmpty(dbDataSource.getUrl()) || JudgeUtil.isEmpty(dbDataSource.getUsername()) || JudgeUtil.isEmpty(dbDataSource.getPassword());
    }


}
