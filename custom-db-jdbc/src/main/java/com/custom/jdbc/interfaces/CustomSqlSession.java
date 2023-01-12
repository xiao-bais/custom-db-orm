package com.custom.jdbc.interfaces;

import com.custom.jdbc.condition.BaseExecutorBody;
import com.custom.jdbc.configuration.DbDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Xiao-Bai
 * @date 2022/10/23 22:37
 * @desc
 */
public interface CustomSqlSession {

    /**
     * 获取当前连接对象
     */
    Connection getConnection() throws SQLException;

    /**
     * 连接连接状态
     */
    void checkConnState(DbDataSource dbDataSource) throws SQLException;

    /**
     * 开启会话
     */
    void openSession() throws SQLException;

    /**
     * 关闭会话
     */
    void closeSession() throws SQLException;

    /**
     * 提交事务
     */
    void commit() throws SQLException;

    /**
     * 回滚事务
     */
    void rollback() throws SQLException;

    /**
     * 关闭资源
     */
    void closeResources() throws SQLException;

    /**
     * 是否为自动提交
     */
    boolean isAutoCommit() throws SQLException;

    /**
     * 执行体
     */
    BaseExecutorBody getBody();

    /**
     * 设置执行体
     */
    void setBody(BaseExecutorBody body);

}
