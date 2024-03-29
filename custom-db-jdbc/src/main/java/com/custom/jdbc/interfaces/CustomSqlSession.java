package com.custom.jdbc.interfaces;

import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.executebody.BaseExecutorBody;
import com.custom.jdbc.handler.ResultSetTypeMappedHandler;
import com.custom.jdbc.session.CustomSqlSessionHelper;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 自定义sql会话接口
 * @author  Xiao-Bai
 * @since  2022/10/23 22:37
 */
public interface CustomSqlSession {

    /**
     * 获取当前连接对象
     */
    Connection getConnection() throws SQLException;

    /**
     * 检查连接状态
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

    /**
     * 获取SqlSession助手
     */
    CustomSqlSessionHelper getHelper();

    /**
     * 结果集映射处理对象
     */
   <T> ResultSetTypeMappedHandler<T> getMappedHandler(Class<T> mappedType);

}
