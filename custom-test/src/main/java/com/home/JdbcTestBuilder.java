package com.home;

import com.custom.action.proxy.JdbcDaoProxy;
import com.custom.action.core.JdbcOpDao;
import com.custom.action.core.JdbcDao;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.configuration.DbGlobalConfig;

/**
 * @author  Xiao-Bai
 * @since  2022/7/8 0008 17:10
 * @Desc
 */
public class JdbcTestBuilder {

    private final DbDataSource dbDataSource;
    private final DbGlobalConfig globalConfig;
    private final DbCustomStrategy dbCustomStrategy;

    public static JdbcTestBuilder builder() {
        return new JdbcTestBuilder();
    }

    private JdbcTestBuilder() {
        // 数据库连接配置
        dbDataSource = new DbDataSource();
        dbDataSource.setUrl("jdbc:mysql://39.108.225.176:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("xh@Mysql1524");

        // 增删改查映射策略配置
        dbCustomStrategy = new DbCustomStrategy();
        dbCustomStrategy.setSqlOutPrinting(true);
//        dbCustomStrategy.setSqlOutPrintExecute(true);
        dbCustomStrategy.setUnderlineToCamel(true);
//        dbCustomStrategy.setDbFieldDeleteLogic("state");
//        dbCustomStrategy.setDeleteLogicValue(1);
//        dbCustomStrategy.setNotDeleteLogicValue(0);

        globalConfig = new DbGlobalConfig();
        globalConfig.setTableNamePrefix("");
        globalConfig.setStrategy(dbCustomStrategy);
        globalConfig.setSqlInterceptor(null);

    }

    public JdbcOpDao getJdbcOpDao() {
        return new JdbcOpDao(dbDataSource, globalConfig);
    }

    public JdbcDao getJdbcDao() {
        return new JdbcDaoProxy(dbDataSource, globalConfig).createProxy();
    }

//    public <T> T getCustomClassDao(Class<T> entityClass) {
//        InterfacesProxyExecutor proxyExecutor = new InterfacesProxyExecutor(dbDataSource, dbCustomStrategy);
//        return proxyExecutor.createProxy(entityClass);
//    }

    public DbDataSource getDbDataSource() {
        return dbDataSource;
    }

    public DbCustomStrategy getDbCustomStrategy() {
        return dbCustomStrategy;
    }

    public DbGlobalConfig getGlobalConfig() {
        return globalConfig;
    }
}
