package com.custom.jdbc.interfaces;

import com.custom.comm.enums.DatabaseType;

/**
 * @author Xiao-Bai
 * @date 2022/10/27 18:01
 * 数据库适配
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
    DatabaseType getType();




}
