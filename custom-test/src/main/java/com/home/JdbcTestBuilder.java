package com.home;

import com.custom.action.interfaces.BaseEntityDao;
import com.custom.action.proxy.JdbcDaoProxy;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.comm.BasicDao;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.custom.proxy.InterfacesProxyExecutor;
import com.home.customtest.dao.CustomTestDao;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/8 0008 17:10
 * @Desc
 */
public class JdbcTestBuilder {

    private final DbDataSource dbDataSource;
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
        dbCustomStrategy.setDbFieldDeleteLogic("state");
        dbCustomStrategy.setDeleteLogicValue(1);
        dbCustomStrategy.setNotDeleteLogicValue(0);


    }

    public JdbcOpDao getJdbcOpDao() {
        JdbcOpDao jdbcDao = new JdbcOpDao(dbDataSource, dbCustomStrategy);
        TableInfoCache.setUnderlineToCamel(true);
        return jdbcDao;
    }

    public JdbcDao getJdbcDao() {
        JdbcDao jdbcDao = new JdbcDaoProxy(dbDataSource, dbCustomStrategy).createProxy();
        TableInfoCache.setUnderlineToCamel(true);
        return jdbcDao;
    }

    public CustomTestDao getCustomTestDao() {
        InterfacesProxyExecutor proxyExecutor = new InterfacesProxyExecutor(dbDataSource, dbCustomStrategy);
        return proxyExecutor.createProxy(CustomTestDao.class);
    }

    public <T> T getCustomClassDao(Class<T> entityClass) {
        InterfacesProxyExecutor proxyExecutor = new InterfacesProxyExecutor(dbDataSource, dbCustomStrategy);
        return proxyExecutor.createProxy(entityClass);
    }
}
