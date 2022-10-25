package com.custom.jdbc.session;

import com.custom.jdbc.exceptions.SQLSessionException;
import com.custom.jdbc.condition.BaseExecutorModel;
import com.custom.jdbc.transaction.DbTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Xiao-Bai
 * @date 2022/10/22 20:17
 * @desc
 */
public class CustomSqlSession implements DbTransaction {

    private static final Logger logger = LoggerFactory.getLogger(CustomSqlSession.class);

    /**
     * 当前连接
     */
    private Connection currentConn;

    /**
     * 执行体
     */
    private final BaseExecutorModel executorModel;

    /**
     * 数据源
     */
    private DataSource dataSource;
    private boolean autoCommit = true;

    public BaseExecutorModel getExecutorModel() {
        return executorModel;
    }

    public CustomSqlSession(Connection currentConn, BaseExecutorModel executorModel) {
        if (currentConn == null) {
            throw new SQLSessionException("No JDBC connection obtained");
        }
        this.currentConn = currentConn;
        this.executorModel = executorModel;
    }

    public CustomSqlSession(BaseExecutorModel executorModel, DataSource dataSource, boolean autoCommit) {
        this.executorModel = executorModel;
        this.dataSource = dataSource;
        this.autoCommit = autoCommit;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (currentConn == null) {
            currentConn = dataSource.getConnection();
            currentConn.setAutoCommit(autoCommit);
        }
        return currentConn;
    }

    @Override
    public void commit() throws SQLException {
        if (currentConn != null && !currentConn.getAutoCommit()) {
            currentConn.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (currentConn != null && !currentConn.getAutoCommit()) {
            currentConn.rollback();
        }
    }

    @Override
    public void closeResources() throws SQLException {
        if (currentConn != null) {
            if (!currentConn.getAutoCommit()) {
                currentConn.setAutoCommit(true);
            }
            currentConn.close();
        }
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }
}
