package com.custom.jdbc.select;

import com.custom.jdbc.condition.SelectSqlParamInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/17 1:06
 * @Desc 基础jdbc查询接口
 */
//
public interface CustomSelectJdbcBasic {

    /**
     * 查询多条记录（通用型）
     */
    <T> List<T> selectList(SelectSqlParamInfo<T> params) throws Exception;

    /**
     * 查询单条记录
     */
    <T> T selectOne(SelectSqlParamInfo<T> params) throws Exception;

    /**
     * 查询单个字段的多结果集（Set）
     */
    <T> Set<T> selectSet(SelectSqlParamInfo<T> params) throws Exception;

    /**
     * 查询单个Map
     */
    <T> Map<String, T> selectMap(SelectSqlParamInfo<T> params) throws Exception;

    /**
     * 通用查询单个对象sql（映射到Map）
     */
    <T> List<Map<String, T>> selectMaps(SelectSqlParamInfo<T> params) throws Exception;

    /**
     * 查询单个字段的多结果集（Arrays）
     */
    <T> T[] selectArrays(SelectSqlParamInfo<T> params) throws Exception;

    /**
     * 查询单个值
     */
    Object selectObj(SelectSqlParamInfo<Object> params) throws Exception;

    /**
     * 查询单个字段的多个值
     */
    List<Object> selectObjs(SelectSqlParamInfo<Object> params) throws Exception;


}
