package com.custom.jdbc.configuration;


import com.alibaba.druid.pool.DruidDataSource;
import com.custom.jdbc.transaction.DbConnGlobal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Xiao-Bai
 * @Date 2021/6/28
 * @Description 数据库连接配置
 */
public class DbConnection {

    private static final Logger logger = LoggerFactory.getLogger(DbConnection.class);

    private DbDataSource dbDataSource = null;
    private static final String CUSTOM_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static Map<String, Object> currMap  = new ConcurrentHashMap<>();

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

    //线程隔离
    private final ThreadLocal<Connection> CONN_LOCAL = new ThreadLocal<>();

    public Connection createConnection() {
        Connection connection = null;
        try {
            connection = CONN_LOCAL.get();
            if (connection == null) {
                DataSource dataSource = (DataSource) currMap.get(DbConnGlobal.getDataSourceKey(dbDataSource));
                connection = dataSource.getConnection();
                CONN_LOCAL.set(connection);
            }

        }catch (SQLException e) {
            logger.error(e.toString(), e);
        }
        return connection;
    }

    public String getDataBase() {
        String dataBaseKey = DbConnGlobal.getDataBaseKey(dbDataSource);
        return currMap.get(dataBaseKey).toString();
    }

    public static Object getCurrMapData(String key) {
        return currMap.get(key);
    }
}
