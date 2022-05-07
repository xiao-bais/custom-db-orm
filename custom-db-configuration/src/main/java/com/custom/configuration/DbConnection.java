package com.custom.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
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
    private static final String CUSTOM_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DATA_BASE = "database";
    @SuppressWarnings(value = "Unckecked")
    public static Map<String, Object> currMap  = new ConcurrentHashMap<>();

    /**
     * 获取连接
     * @param dbDataSource
     */
    public DbConnection(DbDataSource dbDataSource) {
        try {
            isExistClass(dbDataSource.getDriver());
            datasourceInitialize(dbDataSource);
        }catch (Exception e) {
            logger.error("不存在mysql驱动：" + CUSTOM_DRIVER);
            ExThrowsUtil.toCustom(e.getMessage());
        }
    }

    private String getConnKey(DbDataSource dbDataSource) {
        return String.format("%s-%s-%s-%s", dbDataSource.getUrl(), dbDataSource.getUsername(), dbDataSource.getPassword(), dbDataSource.getDatabase());
    }

    private void datasourceInitialize(DbDataSource dbDataSource) throws SQLException {
        connection = (Connection) currMap.get(getConnKey(dbDataSource));
        if (null == connection) {
            initConnection(dbDataSource);
            logger.info("dataSource Connection Successfully !");
        }
        if (JudgeUtilsAx.isEmpty(dbDataSource.getDatabase())) {
            dbDataSource.setDatabase(CustomUtil.getDataBase(dbDataSource.getUrl()));
        }
        currMap.put(DATA_BASE, dbDataSource.getDatabase());
        currMap.put(getConnKey(dbDataSource), connection);

    }

    private void initConnection(DbDataSource dbDataSource) throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(dbDataSource.getDriver());
        druidDataSource.setUrl(dbDataSource.getUrl());
        druidDataSource.setUsername(dbDataSource.getUsername());
        druidDataSource.setPassword(dbDataSource.getPassword());

        druidDataSource.setInitialSize(dbDataSource.getInitialSize());
        druidDataSource.setMinIdle(dbDataSource.getMinIdle());
        druidDataSource.setMaxWait(dbDataSource.getMaxWait());
        druidDataSource.setMaxActive(dbDataSource.getMaxActive());
        druidDataSource.setValidationQuery(dbDataSource.getValidationQuery());
        druidDataSource.setTestWhileIdle(dbDataSource.isTestWhileIdle());
        druidDataSource.setTestOnBorrow(dbDataSource.isTestOnBorrow());
        druidDataSource.setTestOnReturn(dbDataSource.isTestOnReturn());
        connection = druidDataSource.getConnection();
    }


    private void isExistClass(String driverClassName) throws ClassNotFoundException {
        if(JudgeUtilsAx.isEmpty(driverClassName)) {
            driverClassName = CUSTOM_DRIVER;
        }
        Class.forName(driverClassName);
    }

    //线程隔离
    private final ThreadLocal<Connection> CONN_LOCAL = new ThreadLocal<>();

    protected Connection getConnection() {
        try {
            if(null == CONN_LOCAL.get() || connection.isClosed()) {
                CONN_LOCAL.set(connection);
                return connection;
            }
        }catch (SQLException e) {
            return null;
        }
        return CONN_LOCAL.get();
    }

}
