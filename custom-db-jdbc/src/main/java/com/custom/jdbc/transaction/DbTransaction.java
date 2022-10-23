package com.custom.jdbc.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Xiao-Bai
 * @date 2022/10/23 22:37
 * @desc
 */
public interface DbTransaction {

    /**
     * 获取当前连接对象
     */
    Connection getConnection() throws SQLException;

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

}
