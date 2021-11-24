package com.custom.dbconfig;

import com.alibaba.druid.pool.DruidDataSource;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.exceptions.ExceptionConst;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author Xiao-Bai
 * @Date 2021/6/28
 * @Description 数据库连接配置
 */
public class DbConnection {

    private Connection connection = null;
    private DbCustomStrategy dbCustomStrategy;

    /**
     * 获取连接
     * @param dbDataSource
     */
    public DbConnection(DbDataSource dbDataSource) {
        try {
            isExistClass(dbDataSource.getDriver());
            init(dbDataSource);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void init(DbDataSource dbDataSource) throws SQLException {
        connection = (Connection) ExceptionConst.currMap.get(CustomUtil.getConnKey(dbDataSource));
        if (null == connection) {
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
        dbDataSource.setDatabase(CustomUtil.getDataBase(dbDataSource.getUrl()));
        if (JudgeUtilsAx.isEmpty(dbDataSource.getDatabase())) {
            dbDataSource.setDatabase(CustomUtil.getDataBase(dbDataSource.getUrl()));
        }
        ExceptionConst.currMap.put(DbFieldsConst.DATA_BASE, dbDataSource.getDatabase());
        ExceptionConst.currMap.put(CustomUtil.getConnKey(dbDataSource), connection);

        DbCustomStrategy dbCustomStrategy = dbDataSource.getDbCustomStrategy();
        if(null == dbCustomStrategy) {
            dbCustomStrategy = new DbCustomStrategy();
        }
        this.dbCustomStrategy = dbCustomStrategy;
        ExceptionConst.currMap.put(DbFieldsConst.CUSTOM_STRATEGY, dbCustomStrategy);
    }


    private void isExistClass(String driverClassName) throws ClassNotFoundException {
        if(JudgeUtilsAx.isEmpty(driverClassName)) {
            driverClassName = DbFieldsConst.CUSTOM_DRIVER;
        }
        Class.forName(driverClassName);
    }

    //线程隔离
    private static ThreadLocal<Connection> CONN_LOCAL = new ThreadLocal<>();

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

    public DbCustomStrategy getDbCustomStrategy() {
        return dbCustomStrategy;
    }

    public void setDbCustomStrategy(DbCustomStrategy dbCustomStrategy) {
        this.dbCustomStrategy = dbCustomStrategy;
    }
}
