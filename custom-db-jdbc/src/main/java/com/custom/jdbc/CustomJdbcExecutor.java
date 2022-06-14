package com.custom.jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/14 0014 16:23
 * @Desc jdbc执行接口
 */
public interface CustomJdbcExecutor {


    /**
     * 通用查询（Collection）
     * @param clazz class对象
     * @param sqlPrintSupport 支持sql打印
     * @param sql 查询的sql
     * @param params sql参数
     */
    <T> List<T> selectList(Class<T> clazz, boolean sqlPrintSupport, String sql, Object... params) throws Exception;

    /**
     * 查询单个字段的多结果集（Set）
     */
    <T> Set<T> selectSet(Class<T> clazz, String sql, Object... params) throws Exception;

    /**
     * 查询单个Map
     */
    <T> Map<String, T> selectMap(Class<T> clazz, String sql, Object... params) throws Exception;

    /**
     * 查询单个字段的多结果集（Array）
     */
    <T> T[] selectArray(Class<T> clazz, String sql, Object... params) throws Exception;

    /**
     * 查询单个值
     */
    Object selectObjBySql(String sql, Object... params) throws Exception;

    /**
     * 查询单个字段的多个值
     */
    List<Object> selectObjsSql(String sql, Object... params) throws Exception;

    /**
     * 查询指定类型的单个值
     */
    <T> T selectBasicObjBySql(String sql, Object... params) throws Exception;

    /**
     * 查询单个泛型的对象
     */
    <T> T selectGenericObjSql(Class<T> t, String sql, Object... params) throws Exception;

    /**
     * 查询多个map集合
     */
    List<Map<String, Object>> selectMapsBySql(String sql, boolean isSupportMoreResult, Object... params) throws Exception;

    /**
     * 增加、删除、修改
     */
    int executeUpdate(String sql, Object... params) throws Exception;

    /**
     * 增加（自增主键在添加后自动设值）
     */
    <T> int executeInsert(List<T> objs, String sql, String keyField, Class<?> type, Object... params) throws Exception;

    /**
     * 执行创建表结构
     */
    void executeTableSql(String sql);

    /**
     * 增加、删除、修改(执行后，不打印sql)
     */
    void executeUpdateNotPrintSql(String sql) throws SQLException;

    /**
     * 查询表是否存在,字段是否存在
     */
    long executeExist(String sql) throws Exception;







}
