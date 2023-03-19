package com.custom.springboot;

import com.custom.comm.utils.JudgeUtil;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.configuration.DbGlobalConfig;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import com.custom.action.core.structs.TableStructsInitializer;
import com.custom.springboot.scanner.PackageScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 自定义的bean初始化
 * @author  Xiao-Bai
 * @since  2022/5/16 15:02
 *
 **/
@Component
public class CustomBeanInitializer implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(CustomBeanInitializer.class);

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        DbGlobalConfig globalConfig = applicationContext.getBean(DbGlobalConfig.class);
        DbDataSource dataSource = applicationContext.getBean(DbDataSource.class);
        DbCustomStrategy strategy = globalConfig.getStrategy();
        if (!strategy.isSyncEntityEnable()) {
            return;
        }
        String[] packageScans = strategy.getEntityPackageScans();
        if (JudgeUtil.isEmpty(packageScans)) {
            return;
        }
        logger.info("Table info sync process started ... ...");
        // 表结构初始化
        PackageScanner scanner = new PackageScanner(packageScans);
        TableStructsInitializer tableStructsInitializer = new TableStructsInitializer(
                new JdbcSqlSessionFactory(dataSource, globalConfig)
        );
        Set<Class<?>> tableInfoSets = scanner.getBeanRegisterList();
        tableStructsInitializer.initStart(tableInfoSets);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
