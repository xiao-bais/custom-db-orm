package com.custom.springboot;

import com.custom.action.core.JdbcDao;
import com.custom.action.core.JdbcOpDao;
import com.custom.action.proxy.JdbcDaoProxy;
import com.custom.comm.utils.JudgeUtil;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.springboot.scanner.RegisterBeanExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.*;


/**
 * @author  Xiao-Bai
 * @since  2021/11/23 17:47
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
    @Primary
    @ConditionalOnBean(DbDataSource.class)
    public JdbcOpDao jdbcOpDao(){
        if(isDataSourceEmpty(dbDataSource)) {
            return null;
        }
        JdbcOpDao jdbcOpDao = new JdbcOpDao(dbDataSource, dbCustomStrategy);
        logger.info("JdbcOpDao Initialized Successfully !");
        return jdbcOpDao;
    }

    @Bean
    @Primary
    @ConditionalOnBean(DbDataSource.class)
    public JdbcDao jdbcDao() {
        if(isDataSourceEmpty(dbDataSource)) {
            return null;
        }
        JdbcDao jdbcDao = new JdbcDaoProxy(dbDataSource, dbCustomStrategy).createProxy();
        logger.info("JdbcDao Initialized Successfully !");
        return jdbcDao;
    }

    private boolean isDataSourceEmpty(DbDataSource dbDataSource) {
        return JudgeUtil.isEmpty(dbDataSource.getUrl()) || JudgeUtil.isEmpty(dbDataSource.getUsername()) || JudgeUtil.isEmpty(dbDataSource.getPassword());
    }


}
