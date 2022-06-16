package com.custom.jdbc;

import com.custom.jdbc.param.SelectSqlParamInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/17 1:06
 * @Desc
 */
public interface CustomJdbcBasic {

    /**
     * 查询多条记录（通用型）
     */
    <T> List<T> selectList(SelectSqlParamInfo<T> params) throws Exception;

    /**
     * 查询单个字段的多结果集（Set）
     */
    <T> Set<T> selectSet(SelectSqlParamInfo<T> params) throws Exception;

    /**
     * 查询单个Map
     */
    <T> Map<String, T> selectMap(SelectSqlParamInfo<T> params) throws Exception;

    /**
     * 查询单个字段的多结果集（Arrays）
     */
    <T> T[] selectArray(SelectSqlParamInfo<T> params) throws Exception;

    /**
     * 查询单个值
     */
    Object selectObjBySql(SelectSqlParamInfo<Object> params) throws Exception;

    /**
     * 查询单个字段的多个值
     */
    List<Object> selectObjsSql(SelectSqlParamInfo<Object> params) throws Exception;

    /**
     * 查询指定类型的单个值
     */
    <T> T selectBasicObjBySql(SelectSqlParamInfo<T> params) throws Exception;

    /**
     * 查询单个泛型的对象
     */
    <T> T selectGenericObjSql(SelectSqlParamInfo<T> params) throws Exception;

    /**
     * 查询多个map集合
     */
    List<Map<String, Object>> selectMapsBySql(SelectSqlParamInfo<Map<String, Object>> params) throws Exception;


}
