package com.custom.jdbc.session;

import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.exceptions.SQLSessionException;
import com.custom.jdbc.condition.BaseExecutorBody;
import com.custom.jdbc.interfaces.CustomSqlSession;
import com.custom.jdbc.utils.DbConnGlobal;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Xiao-Bai
 * @date 2022/10/22 20:17
 * @desc
 */
public class DefaultSqlSession implements CustomSqlSession {

    /**
     * 当前连接
     */
    private Connection currentConn;

    /**
     * 执行体
     */
    private BaseExecutorBody executorBody;

    public BaseExecutorBody getExecutorBody() {
        return executorBody;
    }

    public DefaultSqlSession(Connection currentConn, BaseExecutorBody executorBody) {
        if (currentConn == null) {
            throw new SQLSessionException("No JDBC connection obtained");
        }
        this.currentConn = currentConn;
        this.executorBody = executorBody;
    }

    public DefaultSqlSession(Connection currentConn) {
        this.currentConn = currentConn;
    }

    @Override
    public Connection getConnection() {
        return currentConn;
    }

    @Override
    public void checkConnState(DbDataSource dbDataSource) throws SQLException {
        if (currentConn == null || currentConn.isClosed()) {
            currentConn = DbConnGlobal.getCurrentConnection(dbDataSource);
        }
    }

    @Override
    public void openSession() throws SQLException {
        if (currentConn != null && currentConn.getAutoCommit()) {
            currentConn.setAutoCommit(false);
        }
    }

    @Override
    public void closeSession() throws SQLException {
        if (currentConn != null && !currentConn.getAutoCommit()) {
            currentConn.setAutoCommit(true);
        }
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
            currentConn.close();
        }
    }

    @Override
    public BaseExecutorBody getBody() {
        return executorBody;
    }

    @Override
    public void setBody(BaseExecutorBody body) {
        this.executorBody = body;
    }

    public boolean isAutoCommit() throws SQLException {
        if (currentConn != null) {
            return currentConn.getAutoCommit();
        }
        return true;
    }
}
