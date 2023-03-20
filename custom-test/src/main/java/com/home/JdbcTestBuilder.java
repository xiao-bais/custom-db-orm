package com.home;

import com.custom.action.core.JdbcDaoProxy;
import com.custom.action.core.JdbcOpDao;
import com.custom.action.core.JdbcDao;
import com.custom.jdbc.configuration.CustomConfigHelper;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.configuration.DbGlobalConfig;
import com.custom.proxy.InterfacesProxyExecutor;

/**
 * @author  Xiao-Bai
 * @since  2022/7/8 0008 17:10
 * @Desc
 */
public class JdbcTestBuilder {

    public static JdbcTestBuilder builder() {
        return new JdbcTestBuilder();
    }

    private JdbcTestBuilder() {



    }

    private CustomConfigHelper configHelper() {

        // 数据库连接配置
        DbDataSource dbDataSource = new DbDataSource();
        dbDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("123456");
        // Driver不用填，默认mysql8.0

        // 全局配置
        DbGlobalConfig globalConfig = new DbGlobalConfig();

        // 策略配置
        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
        // sql打印开关
        dbCustomStrategy.setSqlOutPrinting(true);
        // sql打印时， true为可执行的sql(即参数?已经替换为真实的值)， 默认false
        dbCustomStrategy.setSqlOutPrintExecute(true);
        // 是否下划线转驼峰?
        dbCustomStrategy.setUnderlineToCamel(true);

        // 逻辑删除的字段(表字段)
        dbCustomStrategy.setDbFieldDeleteLogic("state");
        // 逻辑删除的标识值
        dbCustomStrategy.setDeleteLogicValue(1);
        // 未逻辑删除的标识值
        dbCustomStrategy.setNotDeleteLogicValue(0);

        globalConfig.setStrategy(dbCustomStrategy);


        return new CustomConfigHelper(dbDataSource, globalConfig);
    }

    public JdbcOpDao getJdbcOpDao() {
        CustomConfigHelper configHelper = configHelper();
        return new JdbcOpDao(configHelper.getDbDataSource(), configHelper.getDbGlobalConfig());
    }

    public JdbcDao getJdbcDao() {
        CustomConfigHelper configHelper = configHelper();
        return new JdbcDaoProxy(configHelper.getDbDataSource(), configHelper.getDbGlobalConfig()).createProxy();
    }

    public <T> T getCustomClassDao(Class<T> entityClass) {
        InterfacesProxyExecutor proxyExecutor = new InterfacesProxyExecutor();
        return proxyExecutor.createProxy(entityClass);
    }
}
