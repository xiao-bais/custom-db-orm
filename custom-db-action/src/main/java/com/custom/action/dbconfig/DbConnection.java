package com.custom.action.dbconfig;

import com.alibaba.druid.pool.DruidDataSource;
import com.custom.action.comm.CustomUtil;
import com.custom.action.comm.JudgeUtilsAx;
import com.custom.action.exceptions.CustomCheckException;
import com.custom.action.exceptions.ExceptionConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author Xiao-Bai
 * @Date 2021/6/28
 * @Description 数据库连接配置
 */
public class DbConnection {

    private static Logger logger = LoggerFactory.getLogger(DbConnection.class);

    private Connection connection = null;

    /**
     * 获取连接
     * @param dbDataSource
     */
    public DbConnection(DbDataSource dbDataSource) {
        try {
            isExistClass(dbDataSource.getDriver());
            init(dbDataSource);
        }catch (Exception e) {
            logger.error("不存在mysql驱动：" + DbFieldsConst.CUSTOM_DRIVER);
            throw new CustomCheckException(e.getMessage());
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
            logger.info("dataSource Connection Successfully !");
        }
        if (JudgeUtilsAx.isEmpty(dbDataSource.getDatabase())) {
            dbDataSource.setDatabase(CustomUtil.getDataBase(dbDataSource.getUrl()));
        }
        ExceptionConst.currMap.put(DbFieldsConst.DATA_BASE, dbDataSource.getDatabase());
        ExceptionConst.currMap.put(CustomUtil.getConnKey(dbDataSource), connection);

    }


    private void isExistClass(String driverClassName) throws ClassNotFoundException {
        if(JudgeUtilsAx.isEmpty(driverClassName)) {
            driverClassName = DbFieldsConst.CUSTOM_DRIVER;
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
