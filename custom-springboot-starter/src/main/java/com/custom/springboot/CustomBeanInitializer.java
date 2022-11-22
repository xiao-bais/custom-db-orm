package com.custom.springboot;

import com.custom.comm.utils.JudgeUtil;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.springboot.tableinit.TableStructsInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author Xiao-Bai
 * @Date 2022/5/16 15:02
 * @Desc：自定义的bean初始化
 **/
@Component
public class CustomBeanInitializer implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(CustomBeanInitializer.class);

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        DbCustomStrategy strategy = applicationContext.getBean(DbCustomStrategy.class);
        DbDataSource dataSource = applicationContext.getBean(DbDataSource.class);
        if (!strategy.isSyncEntityEnable()) {
            return;
        }
        if (JudgeUtil.isEmpty(strategy.getEntityPackageScans())) {
            return;
        }
        logger.info("Table info sync process started ... ...");
        // 表结构初始化
        TableStructsInitializer tableStructsInitializer = new TableStructsInitializer(
                strategy.getEntityPackageScans(),
                new JdbcExecutorFactory(dataSource, strategy)
        );
        tableStructsInitializer.initStart();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
