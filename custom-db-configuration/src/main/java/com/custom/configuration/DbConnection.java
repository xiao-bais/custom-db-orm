package com.custom.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.exceptions.ExThrowsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private Connection connection = null;
    private DbDataSource dbDataSource = null;
    private DruidDataSource druidDataSource = null;
    private static final String CUSTOM_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DATA_BASE = "database";
    public static Map<String, Object> currMap  = new ConcurrentHashMap<>();

    /**
     * 获取连接
     * @param dbDataSource
     */
    public DbConnection(DbDataSource dbDataSource) {
        try {
            this.dbDataSource = dbDataSource;
            this.loaderDriver();
            this.datasourceInitialize();
        }catch (Exception e) {
            logger.error("不存在mysql驱动：" + CUSTOM_DRIVER);
            ExThrowsUtil.toCustom(e.toString());
        }
    }

    public static String getConnKey(DbDataSource dbDataSource) {
        return String.format("%s-%s-%s-%s",
                dbDataSource.getUrl(),
                dbDataSource.getUsername(),
                dbDataSource.getPassword(),
                dbDataSource.getDatabase()
        );
    }

    private void datasourceInitialize() throws ClassNotFoundException {
        connection = (Connection) currMap.get(getConnKey(dbDataSource));
        if (connection != null) {
            return;
        }
        this.druidDataSource = new DruidDataSource();
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

        if (JudgeUtil.isEmpty(dbDataSource.getDatabase())) {
            if (dbDataSource.getDriver().equals(CUSTOM_DRIVER)) {
                dbDataSource.setDatabase(CustomUtil.getDataBase(dbDataSource.getUrl()));
            }
            else ExThrowsUtil.toCustom("未指定数据库名称");
        }
        currMap.put(DATA_BASE, dbDataSource.getDatabase());
        connection = getConnection();
    }


    private void loaderDriver() throws ClassNotFoundException {
        if(JudgeUtil.isEmpty(dbDataSource.getDriver())) {
            dbDataSource.setDriver(CUSTOM_DRIVER);
        }
        Class.forName(dbDataSource.getDriver());
    }

    //线程隔离
    private final ThreadLocal<Connection> CONN_LOCAL = new ThreadLocal<>();

    public synchronized Connection getConnection() {
        try {
            if(null == CONN_LOCAL.get() || connection.isClosed()) {
                if (druidDataSource != null) {
                    connection = druidDataSource.getConnection();
                    currMap.put(getConnKey(dbDataSource), this.connection);
                    CONN_LOCAL.set(connection);
                }
                return connection;
            }
        }catch (SQLException e) {
            logger.error(e.toString(), e);
            return null;
        }
        return CONN_LOCAL.get();
    }

    public String getDataBase() {
        return currMap.get(DATA_BASE).toString();
    }

}
