package com.custom.dbconfig;

import com.alibaba.druid.pool.DruidDataSource;
import com.custom.comm.CommUtils;
import com.custom.comm.JudgeUtilsAx;
import com.custom.exceptions.ExceptionConst;

import java.sql.Connection;

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
            isExistClass(JudgeUtilsAx.isEmpty(dbDataSource.getDriver()) ? DbFieldsConst.CUSTOM_DRIVER : dbDataSource.getDriver());
            connection = (Connection) ExceptionConst.currMap.get(getConnKey(dbDataSource));
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
            dbDataSource.setDatabase(CommUtils.getDataBase(dbDataSource.getUrl()));
            if (JudgeUtilsAx.isEmpty(dbDataSource.getDatabase())) {
                dbDataSource.setDatabase(CommUtils.getDataBase(dbDataSource.getUrl()));
            }
            ExceptionConst.currMap.put(DbFieldsConst.DATA_BASE, dbDataSource.getDatabase());
            ExceptionConst.currMap.put(getConnKey(dbDataSource), connection);

            DbCustomStrategy dbCustomStrategy = dbDataSource.getDbCustomStrategy();
            if(null == dbCustomStrategy) {
                dbCustomStrategy = new DbCustomStrategy();
            }
            this.dbCustomStrategy = dbCustomStrategy;
            ExceptionConst.currMap.put(DbFieldsConst.CUSTOM_STRATEGY, dbCustomStrategy);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getConnKey(DbDataSource dbDataSource) {
        return String.format("%s-%s-%s-%s", dbDataSource.getUrl(), dbDataSource.getUsername(), dbDataSource.getPassword(), dbDataSource.getDatabase());
    }


    private void isExistClass(String driverClassName) throws ClassNotFoundException {
        try {
            Class.forName(driverClassName);
        }catch (ClassNotFoundException e){
            throw e;
        }
    }

    private static ThreadLocal<Connection> CONN_LOCAL = new ThreadLocal<>();
    protected Connection getConnection() {
        return connection;
    }

    public DbCustomStrategy getDbCustomStrategy() {
        return dbCustomStrategy;
    }

    public void setDbCustomStrategy(DbCustomStrategy dbCustomStrategy) {
        this.dbCustomStrategy = dbCustomStrategy;
    }
}
