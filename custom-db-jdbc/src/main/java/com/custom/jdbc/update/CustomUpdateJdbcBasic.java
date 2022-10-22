package com.custom.jdbc.update;

import com.custom.jdbc.condition.SaveSqlParamInfo;
import com.custom.jdbc.configuretion.DbDataSource;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/18 0:03
 * @Desc 基础jdbc增删改接口
 */
public interface CustomUpdateJdbcBasic {

    /**
     * 通用添加、修改、删除
     */
    int executeUpdate(SaveSqlParamInfo<Object> params) throws Exception;

    /**
     * 插入记录，并为参数中的dataList自动生成主键值
     */
    <T> int executeSave(SaveSqlParamInfo<T> params) throws Exception;

    /**
     * 表结构相关操作，
     * 执行表(字段)结构创建或删除
     */
    void execTableInfo(String sql);

    String getDataBase();

    DbDataSource getDbDataSource();

}
