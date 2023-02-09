package com.custom.jdbc.configuration;


import com.alibaba.druid.pool.DruidDataSource;
import com.custom.jdbc.utils.DbConnGlobal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库连接配置
 * @author  Xiao-Bai
 * @since  2021/6/28
 */
public class DbConnection {

    private static final Logger logger = LoggerFactory.getLogger(DbConnection.class);

    private DbDataSource dbDataSource = null;
    private static final String CUSTOM_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final Map<String, Object> currMap  = new ConcurrentHashMap<>();

    /**
     * 获取连接
     * @param dbDataSource
     */
    public DbConnection(DbDataSource dbDataSource) {
        this.dbDataSource = dbDataSource;
        this.datasourceInitialize();
    }



    private void datasourceInitialize() {
        String dataSourceKey = DbConnGlobal.getDataSourceKey(dbDataSource);
        DruidDataSource cacheDataSource = (DruidDataSource) currMap.get(dataSourceKey);
        if (cacheDataSource != null) {
            return;
        }
        DruidDataSource druidDataSource  = new DruidDataSource();
        druidDataSource.setDriverClassName(dbDataSource.getDriver());
        druidDataSource.setUrl(dbDataSource.getUrl());
        druidDataSource.setUsername(dbDataSource.getUsername());
        druidDataSource.setPassword(dbDataSource.getPassword());
        druidDataSource.setInitialSize(dbDataSource.getInitialSize());
        druidDataSource.setKeepAlive(true);
        druidDataSource.setMinIdle(dbDataSource.getMinIdle());
        druidDataSource.setMaxWait(dbDataSource.getMaxWait());
        druidDataSource.setMaxActive(dbDataSource.getMaxActive());
        druidDataSource.setValidationQuery(dbDataSource.getValidationQuery());
        druidDataSource.setTestWhileIdle(dbDataSource.isTestWhileIdle());
        druidDataSource.setTestOnBorrow(dbDataSource.isTestOnBorrow());
        druidDataSource.setTestOnReturn(dbDataSource.isTestOnReturn());

        currMap.put(DbConnGlobal.getDataBaseKey(dbDataSource), dbDataSource.getDatabase());
        currMap.put(DbConnGlobal.getDataSourceKey(dbDataSource), druidDataSource);
    }


    private static final ThreadLocal<Connection> CONN_LOCAL = new ThreadLocal<>();

    public Connection createConnection() {
        Connection connection = null;
        try {
            connection = CONN_LOCAL.get();
            if (connection == null || connection.isClosed()) {
                String dataSourceKey = DbConnGlobal.getDataSourceKey(dbDataSource);
                DataSource dataSource = (DataSource) currMap.get(dataSourceKey);
                connection = dataSource.getConnection();
                CONN_LOCAL.set(connection);
            }

        }catch (SQLException e) {
            logger.error(e.toString(), e);
        }
        return connection;
    }

    public static Object getCurrMapData(String key) {
        return currMap.get(key);
    }
}
