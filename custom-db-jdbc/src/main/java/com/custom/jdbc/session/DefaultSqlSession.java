package com.custom.jdbc.session;

import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.configuration.DbGlobalConfig;
import com.custom.jdbc.exceptions.SQLSessionException;
import com.custom.jdbc.executebody.BaseExecutorBody;
import com.custom.jdbc.handler.ResultSetTypeMappedHandler;
import com.custom.jdbc.interfaces.CustomSqlSession;
import com.custom.jdbc.utils.DbConnGlobal;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * sql 会话默认实现类
 * @author  Xiao-Bai
 * @since  2022/10/22 20:17
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

    /**
     * 全局配置
     */
    private final DbGlobalConfig globalConfig;

    private final CustomSqlSessionHelper sqlSessionHelper;

    public DefaultSqlSession(DbGlobalConfig globalConfig, Connection currentConn, BaseExecutorBody executorBody) {
        if (currentConn == null) {
            throw new SQLSessionException("No JDBC connection obtained");
        }
        this.globalConfig = globalConfig;
        this.currentConn = currentConn;
        this.executorBody = executorBody;
        this.sqlSessionHelper = new CustomSqlSessionHelper(globalConfig, this);
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

    @Override
    public CustomSqlSessionHelper getHelper() {
        return this.sqlSessionHelper;
    }

    @Override
    public <T> ResultSetTypeMappedHandler<T> getMappedHandler(Class<T> mappedType) {
        return executorBody.createRsMappedHandler(mappedType, globalConfig);
    }

    public boolean isAutoCommit() throws SQLException {
        if (currentConn != null) {
            return currentConn.getAutoCommit();
        }
        return true;
    }
}
