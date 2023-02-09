package com.custom.jdbc.interfaces;

import com.custom.comm.enums.DatabaseDialect;

/**
 * 数据库适配
 * @author  Xiao-Bai
 * @since  2022/10/27 18:01
 */
public interface DatabaseAdapter {

    /**
     * 获取当前数据库名
     */
    String databaseName();


    /**
     * 获取当前连接数据库的驱动类
     */
    String driverClassName();


    /**
     * 获取数据库类型枚举
     */
    DatabaseDialect getType();


    /**
     * 主机地址
     */
    String hostName();


    /**
     * 分页处理
     * @param originSql 查询的SQL
     * @param pageIndex 查询的页数
     * @param pageSize 查询每页大小
     * @return 分页后的SQL
     */
    String handlePage(String originSql, long pageIndex, long pageSize);


    /**
     * 数据库是否存在该表
     * @param table 表名
     */
    boolean existTable(String table);


    /**
     * 表中是否存在该字段
     * @param table 表名称
     * @param column 查询的表字段
     */
    boolean existColumn(String table, String column);




}
