package com.custom.jdbc;

import com.alibaba.fastjson.JSONObject;
import com.custom.comm.CustomUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.custom.jdbc.condition.SelectSqlParamInfo;
import com.custom.jdbc.select.CustomSelectJdbcBasic;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/17 1:15
 * @Desc 基础查询
 */
@SuppressWarnings("unchecked")
public class CustomSelectJdbcBasicImpl extends CustomJdbcManagement implements CustomSelectJdbcBasic {

    private final DbCustomStrategy dbCustomStrategy;

    public CustomSelectJdbcBasicImpl(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        super(dbDataSource, dbCustomStrategy);
        this.dbCustomStrategy = dbCustomStrategy;
    }

    /**
     * 查询多条记录（通用型）
     */
    @Override
    public <T> List<T> selectList(SelectSqlParamInfo<T> params) throws Exception {
        Map<String, Object> map;
        List<T> list = new ArrayList<>();
        try {
            this.statementQuery(params.getPrepareSql(), params.isSqlPrintSupport(), params.getSqlParams());
            ResultSet resultSet = this.handleQueryStatement();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                T t;
                if(CustomUtil.isBasicClass(params.getEntityClass())) {
                    t = (T) resultSet.getObject(SymbolConstant.DEFAULT_ONE);
                }else {
                    map = new HashMap<>();
                    this.handleResultMapper(map, metaData);
                    t = JSONObject.parseObject(JSONObject.toJSONString(map), params.getEntityClass());
                }
                list.add(t);
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(params.getPrepareSql(), params.getSqlParams(), dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        }
        return list;

    }

    /**
     * 查询单条记录
     */
    @Override
    public <T> T selectOne(SelectSqlParamInfo<T> params) throws Exception {
        Map<String, Object> map = new HashMap<>();
        try {
            this.statementQueryReturnRows(params.getPrepareSql(), params.isSqlPrintSupport(), params.getSqlParams());
            ResultSet resultSet = handleQueryStatement();
            this.checkMoreResult();
            ResultSetMetaData metaData = resultSet.getMetaData();
            if (resultSet.next()) {
                this.handleResultMapper(map, metaData);
            }
            if (Map.class.isAssignableFrom(params.getEntityClass())) {
                return (T) map;
            }
            if (map.isEmpty()) return null;
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(params.getPrepareSql(), params.getSqlParams(), dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        }
        return JSONObject.parseObject(JSONObject.toJSONString(map), params.getEntityClass());
    }

    /**
     * 查询单个字段的多结果集（Set）
     */
    @Override
    public <T> Set<T> selectSet(SelectSqlParamInfo<T> params) throws Exception {
        Set<T> resSet = new HashSet<>();
        try {
            statementQuery(params.getPrepareSql(), params.isSqlPrintSupport(), params.getSqlParams());
            ResultSet resultSet = handleQueryStatement();
            while (resultSet.next()) {
                T object = (T) resultSet.getObject(SymbolConstant.DEFAULT_ONE);
                resSet.add(object);
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(params.getPrepareSql(), params.getSqlParams(), dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        }
        return resSet;
    }

    /**
     * 查询单个Map
     */
    @Override
    public <T> Map<String, T> selectMap(SelectSqlParamInfo<T> params) throws Exception {
        Map<String, T> resMap = new HashMap<>();
        try {
            statementQuery(params.getPrepareSql(), params.isSqlPrintSupport(), params.getSqlParams());
            ResultSet resultSet = handleQueryStatement();
            if (resultSet.next()) {
                this.handleResultMapper(resMap, resultSet.getMetaData());
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(params.getPrepareSql(), params.getSqlParams(), dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        }
        return resMap;
    }

    /**
     * 通用查询单个对象sql（映射到Map）
     */
    @Override
    public <T> List<Map<String, T>> selectMaps(SelectSqlParamInfo<T> params) throws Exception {
        Map<String, T> resMap;
        List<Map<String, T>> mapList = new ArrayList<>();
        try {
            statementQuery(params.getPrepareSql(), params.isSqlPrintSupport(), params.getSqlParams());
            ResultSet resultSet = handleQueryStatement();
            if (!params.isSupportMoreResult()) {
                this.checkMoreResult();
            }
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                resMap = new HashMap<>();
                this.handleResultMapper(resMap, metaData);
                mapList.add(resMap);
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(params.getPrepareSql(), params.getSqlParams(), dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        }
        return mapList;
    }

    /**
     * 查询单个字段的多结果集（Arrays）
     */
    @Override
    public <T> T[] selectArrays(SelectSqlParamInfo<T> params) throws Exception {
        try {
            this.statementQueryReturnRows(params.getPrepareSql(), params.isSqlPrintSupport(), params.getSqlParams());
            ResultSet resultSet = handleQueryStatement();
            int rowsCount = this.getRowsCount();
            int count = resultSet.getMetaData().getColumnCount();
            if (count == 0) {
                return null;
            } else if (count > 1) {
                ExThrowsUtil.toCustom("数组不支持返回多列结果");
            }

            Object res = Array.newInstance(params.getEntityClass(), rowsCount);
            int len = 0;
            while (resultSet.next()) {
                T val = (T) resultSet.getObject(SymbolConstant.DEFAULT_ONE);
                Array.set(res, len, val);
                len++;
            }
            return (T[])res;
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(params.getPrepareSql(), params.getSqlParams(), dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        }
    }


    /**
     * 查询单个字段的单个值
     */
    @Override
    public Object selectObj(SelectSqlParamInfo<Object> params) throws Exception {
        Object result = null;
        try {
            this.statementQueryReturnRows(params.getPrepareSql(), params.isSqlPrintSupport(), params.getSqlParams());
            ResultSet resultSet = handleQueryStatement();
            this.checkMoreResult();
            if (resultSet.next()) {
                result = resultSet.getObject(SymbolConstant.DEFAULT_ONE);
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(params.getPrepareSql(), params.getSqlParams(), dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        }
        return result;
    }

    /**
     * 查询单个字段的多条记录
     */
    @Override
    public List<Object> selectObjs(SelectSqlParamInfo<Object> params) throws Exception {
        List<Object> result = new ArrayList<>();
        try {
            statementQuery(params.getPrepareSql(), params.isSqlPrintSupport(), params.getSqlParams());
            ResultSet resultSet = handleQueryStatement();
            while (resultSet.next()) {
                result.add(resultSet.getObject(SymbolConstant.DEFAULT_ONE));
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(params.getPrepareSql(), params.getSqlParams(), dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        }
        return result;
    }

}
