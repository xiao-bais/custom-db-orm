package com.custom.handler;

import com.alibaba.fastjson.JSONObject;
import com.custom.comm.CustomUtil;
import com.custom.comm.SqlOutPrintBuilder;
import com.custom.dbconfig.DbConnection;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.SymbolConst;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/9 0009 3:11
 * @Version 1.0
 * @Description SqlExecuteHandler
 */
public class SqlExecuteHandler extends DbConnection {

    private Connection conn;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private DbParserFieldHandler parserFieldHandler;
    private DbCustomStrategy dbCustomStrategy;

    public SqlExecuteHandler(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy, DbParserFieldHandler parserFieldHandler) {
        super(dbDataSource);
        this.conn = super.getConnection();
        this.dbCustomStrategy = dbCustomStrategy;
        this.parserFieldHandler = parserFieldHandler;
    }

    public DbParserFieldHandler getParserFieldHandler() {
        return parserFieldHandler;
    }

    /**
     * 预编译-更新
     */
    private void statementUpdate(boolean isSave, String sql, Object... params) throws Exception {
        statement = isSave ? conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) : conn.prepareStatement(sql);
        if (dbCustomStrategy.isSqlOutPrinting() && dbCustomStrategy.isSqlOutUpdate()) {
            new SqlOutPrintBuilder(sql, params).sqlInfoUpdatePrint();
        }
        if (params.length <= 0) return;
        for (int i = 0; i < params.length; i++) {
            statement.setObject((i + 1), params[i]);
        }
    }

    /**
     * 预编译-查询1
     */
    private void statementQuery(String sql, Object... params) throws Exception {
        statement = conn.prepareStatement(sql);
        if (dbCustomStrategy.isSqlOutPrinting()) {
            new SqlOutPrintBuilder(sql, params).sqlInfoQueryPrint();
        }
        if (params.length <= 0) return;
        for (int i = 0; i < params.length; i++) {
            statement.setObject((i + 1), params[i]);
        }
    }

    /**
    * 预编译-查询2（可预先获取结果集行数）
    */
    private void statementQuery2(String sql, Object... params) throws Exception {
        String execSql = CustomUtil.prepareSql(sql, params);
        statement = conn.prepareStatement(execSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        if (dbCustomStrategy.isSqlOutPrinting()) {
            new SqlOutPrintBuilder(sql, params).sqlInfoQueryPrint();
        }
    }


    /**
     * 通用查询（Collection）
     */
    protected <T> List<T> query(Class<T> clazz, String sql, Object... params) throws Exception {
        Map<String, Object> map;
        List<T> list = new ArrayList<>();
        try {
            statementQuery(sql, params);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                map = new HashMap<>();
                getResultMap(map, metaData);
                T t = dbCustomStrategy.isUnderlineToCamel() ? JSONObject.parseObject(JSONObject.toJSONString(map), clazz)
                        : CustomUtil.mapToObject(clazz, map);
                list.add(t);
            }
        } catch (SQLException e) {
            new SqlOutPrintBuilder(sql, params).sqlErrPrint();
            throw e;
        }
        return list;

    }

    /**
     * 查询单个字段的多结果集（Set）
     */
    @SuppressWarnings("unchecked")
    protected <T> Set<T> querySet(Class<T> t, String sql, Object... params) throws Exception {
        Set<T> resSet = new HashSet<>();
        try {
            statementQuery(sql, params);
            resultSet = statement.executeQuery();
            if (resultSet.getMetaData().getColumnCount() > 1) {
                throw new CustomCheckException(String.format(ExceptionConst.EX_QUERY_SET_RESULT, t.getTypeName()));
            }
            while (resultSet.next()) {
                T object = (T) resultSet.getObject(1);
                resSet.add(object);
            }
        } catch (SQLException e) {
            new SqlOutPrintBuilder(sql, params).sqlErrPrint();
            throw e;
        }
        return resSet;
    }

    /**
     * 查询单个字段的多结果集（Array）
     */
    @SuppressWarnings("unchecked")
    protected <T> T[] queryArray(Class<T> t, String sql, String className, String methodName, Object... params) throws Exception {
        T[] resEntity;
        try {
            statementQuery2(sql, params);
            resultSet = statement.executeQuery();
            resultSet.last();
            final int rowsCount = resultSet.getRow();
            resultSet.beforeFirst();
            int count = this.resultSet.getMetaData().getColumnCount();
            if (count == 0) {
                return null;
            } else if (count > 1) {
                throw new CustomCheckException(ExceptionConst.EX_QUERY_ARRAY_RESULT);
            }

            resEntity = (T[]) Array.newInstance(t, rowsCount);
            int len = 0;
            while (this.resultSet.next()) {
                T object = (T) this.resultSet.getObject(1);
                resEntity[len] = object;
                len++;
            }
        } catch (SQLException e) {
            new SqlOutPrintBuilder(sql, params).sqlErrPrint();
            throw e;
        }catch (RuntimeException e) {
            if(e instanceof ClassCastException && t.isPrimitive()) {
                throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_SUPPORT_USE_BASIC_TYPE, className, methodName));
            }
            throw e;
        }
        return resEntity;
    }

    /**
     * 获取结果集对象
     */
    private void getResultMap(Map<String, Object> map, ResultSetMetaData metaData) throws SQLException {
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnLabel(i + 1);
            Object object = resultSet.getObject(i + 1);
            map.put(dbCustomStrategy.isUnderlineToCamel() ? CustomUtil.underlineToCamel(columnName) : columnName, object);
        }
    }


    /**
     * 查询单个值SQL
     */
    protected Object selectOneSql(String sql, Object... params) throws Exception {
        Object result = null;
        try {
            statementQuery(sql, params);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getObject(SymbolConst.DEFAULT_ONE);
            }
        } catch (SQLException e) {
            new SqlOutPrintBuilder(sql, params).sqlErrPrint();
            throw e;
        }
        return result;
    }

    /**
     * 通用查询单个对象sql
     */
    @SuppressWarnings("unchecked")
    protected <T> T selectOneSql(Class<T> t, String sql, Object... params) throws Exception {
        Map<String, Object> map = new HashMap<>();
        try {
            statementQuery(sql, params);
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
            new SqlOutPrintBuilder(sql, params).sqlErrPrint();
            throw e;
        }
        return dbCustomStrategy.isUnderlineToCamel() ? JSONObject.parseObject(JSONObject.toJSONString(map), t)
                : CustomUtil.mapToObject(t, map);
    }


    /**
     * 通用删 /改
     */
    protected int executeUpdate(String sql, Object... params) throws Exception {
        int res;
        try {
            statementUpdate(false, sql, params);
            res = statement.executeUpdate();
        } catch (SQLException e) {
            new SqlOutPrintBuilder(sql, params).sqlErrPrint();
            throw e;
        }
        return res;
    }

    /**
     * 插入
     */
    @SuppressWarnings("Unchecked")
    protected <T> int executeInsert(List<T> obj, String sql, String keyField, Object... params) throws Exception {
        int res;
        try {
            statementUpdate(true, sql, params);
            res = statement.executeUpdate();
        } catch (SQLException e) {
            new SqlOutPrintBuilder(sql, params).sqlErrPrint();
            throw e;
        }
        resultSet = statement.getGeneratedKeys();
        int count = 0;
        while (resultSet.next()) {
            T t = obj.get(count);
            Field fieldKeyType = parserFieldHandler.getFieldKeyType(t.getClass());
            Class<?> type = fieldKeyType.getType();
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
     * 执行表结构创建或删除
     */
    protected void executeTableSql(String sql) throws SQLException {
        statement = conn.prepareStatement(sql);
        statement.execute();
    }

    /**
     * 查询表是否存在
     */
    protected long executeTableExist(String sql) throws SQLException {
        long count = 0;
        statement = conn.prepareStatement(sql);
        resultSet = statement.executeQuery();
        if (resultSet.next()) {
            count = (long) resultSet.getObject(SymbolConst.DEFAULT_ONE);
        }
        return count;
    }


}
