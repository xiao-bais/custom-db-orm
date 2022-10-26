package com.custom.jdbc.configuration;


import com.alibaba.druid.pool.DruidDataSource;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.exceptions.ExThrowsUtil;
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
    public static Map<String, Object> currMap  = new ConcurrentHashMap<>();

    /**
     * 获取连接
     * @param dbDataSource
     */
    public DbConnection(DbDataSource dbDataSource) {
        try {
            this.dbDataSource = dbDataSource;
            this.loadDriver();
            this.datasourceInitialize();
        }catch (Exception e) {
            logger.error("不存在mysql驱动：" + CUSTOM_DRIVER);
            ExThrowsUtil.toCustom(e.toString());
        }
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

        if (JudgeUtil.isEmpty(dbDataSource.getDatabase())) {
            if (dbDataSource.getDriver().equals(CUSTOM_DRIVER)) {
                dbDataSource.setDatabase(CustomUtil.getDataBase(dbDataSource.getUrl()));
            }
            else ExThrowsUtil.toCustom("未指定数据库名称");
        }
        currMap.put(DbConnGlobal.getDataBaseKey(dbDataSource), dbDataSource.getDatabase());
        currMap.put(DbConnGlobal.getDataSourceKey(dbDataSource), druidDataSource);
    }


    private void loadDriver() throws ClassNotFoundException {
        if(JudgeUtil.isEmpty(dbDataSource.getDriver())) {
            dbDataSource.setDriver(CUSTOM_DRIVER);
        }
        Class.forName(dbDataSource.getDriver());
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
}