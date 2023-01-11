package com.custom.jdbc.session;

import com.custom.jdbc.exceptions.SQLSessionException;
import com.custom.jdbc.condition.BaseExecutorBody;
import com.custom.jdbc.interfaces.CustomSqlSession;

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
    private boolean autoCommit = true;

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


    public DefaultSqlSession(Connection connection, BaseExecutorBody executorBody, boolean autoCommit) {
        this.executorBody = executorBody;
        this.autoCommit = autoCommit;
    }

    @Override
    public Connection getConnection() {
        return currentConn;
    }

    @Override
    public void openTrans() throws SQLException {
        if (currentConn != null && currentConn.getAutoCommit()) {
            System.out.println("开启连接:" + currentConn);
            currentConn.setAutoCommit(false);
        }
    }

    @Override
    public void commit() throws SQLException {
        if (currentConn != null && !currentConn.getAutoCommit()) {
            System.out.println("连接提交:" + currentConn);
            currentConn.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (currentConn != null && !currentConn.getAutoCommit()) {
            System.out.println("连接事务回滚:" + currentConn);
            currentConn.rollback();
        }
    }

    @Override
    public void closeResources() throws SQLException {
        if (currentConn != null) {
            System.out.println("连接关闭:" + currentConn);
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
        return autoCommit;
    }
}
