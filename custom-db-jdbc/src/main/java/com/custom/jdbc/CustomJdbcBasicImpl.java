package com.custom.jdbc;

import com.alibaba.fastjson.JSONObject;
import com.custom.comm.CustomUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.configuration.DbConnection;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.custom.jdbc.interfaces.CustomJdbcBasicSelect;
import com.custom.jdbc.interfaces.CustomJdbcBasicUpdate;
import com.custom.jdbc.param.SaveSqlParamInfo;
import com.custom.jdbc.param.SelectSqlParamInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/17 1:15
 * @Desc
 */
@SuppressWarnings("unchecked")
public class CustomJdbcBasicImpl extends DbConnection
        implements CustomJdbcBasicSelect, CustomJdbcBasicUpdate {

    private static final Logger logger = LoggerFactory.getLogger(JdbcExecutorImpl.class);

    private final Connection conn;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private final DbCustomStrategy dbCustomStrategy;

//    private static ThreadLocal<Boolean> AUTO_COMMENT = new ThreadLocal<>();

    public CustomJdbcBasicImpl(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        super(dbDataSource);
        this.conn = super.getConnection();
        this.dbCustomStrategy = dbCustomStrategy;
    }

    public DbCustomStrategy getDbCustomStrategy() {
        return dbCustomStrategy;
    }

    /**
     * 预编译-更新
     */
    private void statementUpdate(boolean isSave, String sql, Object... params) throws Exception {
        statement = isSave ? conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) : conn.prepareStatement(sql);
        if (dbCustomStrategy.isSqlOutPrinting()) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlInfoUpdatePrint();
        }
        if (params.length <= 0) return;
        for (int i = 0; i < params.length; i++) {
            statement.setObject((i + 1), params[i]);
        }
    }

    /**
     * 预编译-查询1
     */
    private void statementQuery(String sql, boolean sqlPrintSupport, Object... params) throws Exception {
        statement = conn.prepareStatement(sql);
        if (dbCustomStrategy.isSqlOutPrinting() && sqlPrintSupport) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlInfoQueryPrint();
        }
        if (params.length <= 0) return;
        for (int i = 0; i < params.length; i++) {
            statement.setObject((i + 1), params[i]);
        }
    }

    /**
     * 预编译-查询2（可预先获取结果集行数）
     */
    private void statementQueryReturnRows(String sql, Object... params) throws Exception {
        statement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        for (int i = 0; i < params.length; i++) {
            statement.setObject((i + 1), params[i]);
        }
        if (dbCustomStrategy.isSqlOutPrinting()) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlInfoQueryPrint();
        }
    }

    /**
     * 处理结果集对象
     */
    private <T> void handleResultMap(Map<String, T> map, ResultSetMetaData metaData) throws SQLException {
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnLabel(i + 1);
            T object = (T) resultSet.getObject(i + 1);
            map.put(columnName, object);
        }
    }

    /**
     * 检查是否多结果
     */
    private void checkMoreResult() throws SQLException {
        resultSet.last();
        final int rowsCount = resultSet.getRow();
        resultSet.beforeFirst();
        if (rowsCount > SymbolConstant.DEFAULT_ONE) {
            ExThrowsUtil.toCustom("只查一条，但查询到多条结果：(%s)", rowsCount);
        }
    }

    /**
     * 查询多条记录（通用型）
     */
    @Override
    public <T> List<T> selectList(SelectSqlParamInfo<T> params) throws Exception {
        Map<String, Object> map;
        List<T> list = new ArrayList<>();
        try {
            statementQuery(params.getPrepareSql(), params.isSqlPrintSupport(), params.getSqlParams());
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                T t;
                if(CustomUtil.isBasicClass(params.getEntityClass())) {
                    t = (T) resultSet.getObject(SymbolConstant.DEFAULT_ONE);
                }else {
                    map = new HashMap<>();
                    this.handleResultMap(map, metaData);
                    t = JSONObject.parseObject(JSONObject.toJSONString(map), params.getEntityClass());
                }
                list.add(t);
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder.build(params.getPrepareSql(), params.getSqlParams(), dbCustomStrategy.isSqlOutPrintExecute()).sqlErrPrint();
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
            statementQuery(params.getPrepareSql(), params.isSqlPrintSupport(), params.getSqlParams());
            resultSet = statement.executeQuery();
            this.checkMoreResult();
            ResultSetMetaData metaData = resultSet.getMetaData();
            if (resultSet.next()) {
                this.handleResultMap(map, metaData);
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
            resultSet = statement.executeQuery();
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
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                this.handleResultMap(resMap, resultSet.getMetaData());
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
        Map<String, T> map;
        List<Map<String, T>> mapList = new ArrayList<>();
        try {
            statementQuery(params.getPrepareSql(), params.isSqlPrintSupport(), params.getSqlParams());
            resultSet = statement.executeQuery();
            if (!params.isSupportMoreResult()) {
                this.checkMoreResult();
            }
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                map = new HashMap<>();
                this.handleResultMap(map, metaData);
                mapList.add(map);
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
            statementQueryReturnRows(params.getPrepareSql(), params.getSqlParams());
            resultSet = statement.executeQuery();
            resultSet.last();
            final int rowsCount = resultSet.getRow();
            resultSet.beforeFirst();
            int count = this.resultSet.getMetaData().getColumnCount();
            if (count == 0) {
                return null;
            } else if (count > 1) {
                ExThrowsUtil.toCustom("数组不支持返回多列结果");
            }

            Object res = java.lang.reflect.Array.newInstance(params.getEntityClass(), rowsCount);
            int len = 0;
            while (this.resultSet.next()) {
                T val = (T) this.resultSet.getObject(SymbolConstant.DEFAULT_ONE);
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
            statementQuery(params.getPrepareSql(), params.isSqlPrintSupport(), params);
            resultSet = statement.executeQuery();
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
            resultSet = statement.executeQuery();
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


    /**
     * 通用添加、修改、删除
     */
    @Override
    public int executeUpdate(SaveSqlParamInfo<Object> params) throws Exception {
        int res;
        try {
            statementUpdate(false, params.getPrepareSql(), params.getSqlParams());
            res = statement.executeUpdate();
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(params.getPrepareSql(), params.getSqlParams(), dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        }
        return res;
    }

    /**
     * 插入
     */
    public <T> int executeSave(SaveSqlParamInfo<T> params) throws Exception {
        int res;
        try {
            statementUpdate(true, params.getPrepareSql(), params.getSqlParams());
            res = statement.executeUpdate();
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(params.getPrepareSql(), params.getSqlParams(), dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        }
        resultSet = statement.getGeneratedKeys();
        int count = 0;
        Field keyField = params.getKeyField();
        while (resultSet.next()) {
            T t = params.getDataList().get(count);
            PropertyDescriptor pd = new PropertyDescriptor(keyField.getName(), t.getClass());
            Method writeMethod = pd.getWriteMethod();
            String val = String.valueOf(resultSet.getObject(1));
            if (Long.class.isAssignableFrom(keyField.getType()) || keyField.getType().equals(Long.TYPE)) {
                writeMethod.invoke(t, Long.parseLong(val));
            } else if (Integer.class.isAssignableFrom(keyField.getType()) || keyField.getType().equals(Integer.TYPE)) {
                writeMethod.invoke(t, Integer.parseInt(val));
            }
            // else ignore...
            count++;
        }
        return res;
    }

    /**
     * 执行表(字段)结构创建或删除
     */
    public void execTableInfo(String sql) {
        try {
            statement = conn.prepareStatement(sql);
            statement.execute();
        }catch (Exception e) {
            SqlOutPrintBuilder
                    .build(sql, new String[]{}, dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            logger.error(e.toString(), e);
        }
    }

}
