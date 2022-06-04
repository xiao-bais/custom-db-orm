package com.custom.jdbc;

import com.alibaba.fastjson.JSONObject;
import com.custom.comm.CustomUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.configuration.DbConnection;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/9 0009 3:11
 * @Version 1.0
 * @Description SqlExecuteHandler
 */
public class ExecuteSqlHandler extends DbConnection {

    private static final Logger logger = LoggerFactory.getLogger(ExecuteSqlHandler.class);

    private final Connection conn;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private boolean autoCommit = true;
    private final DbCustomStrategy dbCustomStrategy;

    public ExecuteSqlHandler(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
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
//        String execSql = CustomUtil.prepareSql(sql, params);
        statement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        for (int i = 0; i < params.length; i++) {
            statement.setObject((i + 1), params[i]);
        }
        if (dbCustomStrategy.isSqlOutPrinting()) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlInfoQueryPrint();
        }
    }

    /**
     * 通用查询（Collection）
     * @param clazz class对象
     * @param sqlPrintSupport 支持sql打印
     * @param sql 查询的sql
     * @param params sql参数
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> query(Class<T> clazz, boolean sqlPrintSupport, String sql, Object... params) throws Exception {
        Map<String, Object> map;
        List<T> list = new ArrayList<>();
        try {
            statementQuery(sql, sqlPrintSupport, params);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                T t;
                if(CustomUtil.isBasicClass(clazz)) {
                   t = (T) resultSet.getObject(SymbolConstant.DEFAULT_ONE);
                }else {
                    map = new HashMap<>();
                    getResultMap(map, metaData);
                    t = JSONObject.parseObject(JSONObject.toJSONString(map), clazz);
                }
                list.add(t);
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlErrPrint();
            throw e;
        }
        return list;

    }

    /**
     * 查询单个字段的多结果集（Set）
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> querySet(Class<T> t, String sql, Object... params) throws Exception {
        Set<T> resSet = new HashSet<>();
        try {
            statementQuery(sql, true, params);
            resultSet = statement.executeQuery();
            if (resultSet.getMetaData().getColumnCount() > 1) {
                ExThrowsUtil.toCustom(String.format("The 'Set<%s>' does not support returning multiple column results", t.getTypeName()));
            }
            while (resultSet.next()) {
                T object = (T) resultSet.getObject(1);
                resSet.add(object);
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlErrPrint();
            throw e;
        }
        return resSet;
    }

    /**
     * 查询单个Map
     * @param t
     * @param sql
     * @param params
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> Map<String, T> queryMap(Class<T> t, String sql, Object... params) throws Exception {
        Map<String, T> resMap = new HashMap<>();
        try {
            statementQuery(sql, true, params);
            resultSet = statement.executeQuery();
            if (resultSet.getMetaData().getColumnCount() > 1) {
                ExThrowsUtil.toCustom(String.format("The 'Set<%s>' does not support returning multiple column results", t.getTypeName()));
            }
            if (resultSet.next()) {
                getResultMap(resMap, resultSet.getMetaData());
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlErrPrint();
            throw e;
        }
        return resMap;
    }

    /**
     * 查询单个字段的多结果集（Array）
     */
    @SuppressWarnings("unchecked")
    public  <T> T[] queryArray(Class<T> t, String sql, Object... params) throws Exception {
        try {
            statementQueryReturnRows(sql, params);
            resultSet = statement.executeQuery();
            resultSet.last();
            final int rowsCount = resultSet.getRow();
            resultSet.beforeFirst();
            int count = this.resultSet.getMetaData().getColumnCount();
            if (count == 0) {
                return null;
            } else if (count > 1) {
                ExThrowsUtil.toCustom("The 'Arrays' does not support returning multiple column results");
            }

            Object res = Array.newInstance(t, rowsCount);
            int len = 0;
            while (this.resultSet.next()) {
                T val = (T) this.resultSet.getObject(1);
                Array.set(res, len, val);
                len++;
            }
            return (T[])res;
        } catch (SQLException e) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlErrPrint();
            throw e;
        }
    }

    /**
     * 获取结果集对象
     */
    @SuppressWarnings("unchecked")
    private <T> void getResultMap(Map<String, T> map, ResultSetMetaData metaData) throws SQLException {
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnLabel(i + 1);
            T object = (T) resultSet.getObject(i + 1);
            map.put(columnName, object);
        }
    }


    /**
     * 查询单个值SQL
     */
    public Object selectObjSql(String sql, Object... params) throws Exception {
        Object result = null;
        try {
            statementQuery(sql, true, params);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getObject(SymbolConstant.DEFAULT_ONE);
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlErrPrint();
            throw e;
        }
        return result;
    }

    /**
     * 查询单个值SQL
     */
    public List<Object> selectObjsSql(String sql, Object... params) throws Exception {
        List<Object> result = new ArrayList<>();
        try {
            statementQuery(sql, true, params);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getObject(SymbolConstant.DEFAULT_ONE));
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlErrPrint();
            throw e;
        }
        return result;
    }

    /**
     * 通用查询单个对象sql
     */
    @SuppressWarnings("unchecked")
    public <T> T selectObjSql(Class<T> t, String sql, Object... params) throws Exception {
        Map<String, Object> map = new HashMap<>();
        try {
            statementQuery(sql, true, params);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            if (resultSet.next()) {
                getResultMap(map, metaData);
            }
            if (map.isEmpty()) return null;
            if (Map.class.isAssignableFrom(t)) {
                return (T) map;
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlErrPrint();
            throw e;
        }
        return JSONObject.parseObject(JSONObject.toJSONString(map), t);
    }

    /**
     * 通用查询单个对象sql（映射到Map）
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> selectMapsSql(String sql, boolean isSupportMoreResult, Object... params) throws Exception {
        Map<String, Object> map;
        List<Map<String, Object>> mapList = new ArrayList<>();
        try {
            statementQuery(sql, true, params);
            resultSet = statement.executeQuery();
            if (!isSupportMoreResult) {
                resultSet.last();
                final int rowsCount = resultSet.getRow();
                if (rowsCount > 1) {
                    ExThrowsUtil.toCustom(String.format("One was queried, but more were found:(%s) ", rowsCount));
                }
                resultSet.beforeFirst();
            }
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                map = new HashMap<>();
                getResultMap(map, metaData);
                mapList.add(map);
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlErrPrint();
            throw e;
        }
        return mapList;
    }


    /**
     * 通用删 /改
     */
    public int executeUpdate(String sql, Object... params) throws Exception {
        int res;
        try {
            statementUpdate(false, sql, params);
            res = statement.executeUpdate();
        } catch (SQLException e) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlErrPrint();
            throw e;
        }
        return res;
    }

    /**
     * 插入
     */
    @SuppressWarnings("Unchecked")
    public <T> int executeInsert(List<T> obj, String sql, String keyField, Class<?> type, Object... params) throws Exception {
        int res;
        try {
            statementUpdate(true, sql, params);
            res = statement.executeUpdate();
        } catch (SQLException e) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlErrPrint();
            throw e;
        }
        resultSet = statement.getGeneratedKeys();
        int count = 0;
        while (resultSet.next()) {
            T t = obj.get(count);
            PropertyDescriptor pd = new PropertyDescriptor(keyField, t.getClass());
            Method writeMethod = pd.getWriteMethod();
            if (type.equals(Long.class) || type.equals(long.class)) {
                long key = Long.parseLong(resultSet.getObject(1).toString());
                writeMethod.invoke(t, key);
            } else if (type.equals(Integer.class) || type.equals(int.class)) {
                int key = Integer.parseInt(resultSet.getObject(1).toString());
                writeMethod.invoke(t, key);
            }
            count++;
        }
        return res;
    }

    /**
     * 执行表(字段)结构创建或删除
     */
    public void executeTableSql(String sql) {
        try {
            statement = conn.prepareStatement(sql);
            statement.execute();
        }catch (Exception e) {
            SqlOutPrintBuilder.build(sql, new String[]{}, dbCustomStrategy.isSqlOutPrintExecute()).sqlErrPrint();
            logger.error(e.toString(), e);
        }
    }

    /**
     * 直接执行，不打印
     */
    public void executeUpdateNotPrintSql(String sql) throws SQLException {
        statement = conn.prepareStatement(sql);
        statement.execute();
    }

    /**
     * 查询表是否存在,字段是否存在
     */
    public long executeExist(String sql) throws Exception {
        long count = 0;
        statementQuery(sql, false);
        resultSet = statement.executeQuery();
        if (resultSet.next()) {
            count = (long) resultSet.getObject(SymbolConstant.DEFAULT_ONE);
        }
        return count;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }
}
