package com.custom.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.custom.dbconfig.DbConnection;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.comm.CommUtils;
import com.custom.dbconfig.SymbolConst;
import com.custom.test.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
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

    private Logger logger = LoggerFactory.getLogger(SqlExecuteHandler.class);


    private Connection conn;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private DbParserFieldHandler parserFieldHandler;
    private DbCustomStrategy dbCustomStrategy;

    public SqlExecuteHandler(DbDataSource dbDataSource, DbParserFieldHandler parserFieldHandler){
        super(dbDataSource);
        conn = super.getConnection();
        dbCustomStrategy = super.getDbCustomStrategy();
        this.parserFieldHandler = parserFieldHandler;
    }

    private void executeAll(boolean isSave,String sql, Object... params) throws Exception {
        statement = isSave ? conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) : conn.prepareStatement(sql);
        if (dbCustomStrategy.isSqlOutPrinting()) {

            if (params.length <= 0) return;
            for (int i = 0; i < params.length; i++) {
                statement.setObject((i + 1), params[i]);
            }
        }
    }


    /**
     * PRINT-ERROR-SQL
     */
    private void sqlPrint(String sql, Object... params) {
        logger.info(
                "\nsql error\n===================\nSQL ====>\n {}\n===================\nparams = {}\n"
                , sql, JSON.toJSONString(params));
    }

//   <T> List<T> query(Class<T> clazz, String sql, Object... params) throws Exception{
//        Map<String, Object> map;
//        List<T> list = new ArrayList<>();
//        T entity;
//        map = new HashMap<>();
//        try{
//            executeAll(false, sql, params);
//            resultSet = statement.executeQuery();
//            ResultSetMetaData metaData = resultSet.getMetaData();
//            while (resultSet.next()) {
//                map.clear();
//                for (int i = 0; i < metaData.getColumnCount(); i++) {
//                    String columnName = metaData.getColumnLabel(i + 1);
//                    Object object = resultSet.getObject(i + 1);
//                    map.put(dbCustomStrategy.isUnderlineToCamel() ? CommUtils.underlineToCamel(columnName) : columnName, object);
//                }
//                if (map.size() <= 0)  continue;
//                //利用反射去实例化对象
//                entity = clazz.newInstance();
//                for (Map.Entry<String, Object> entry : map.entrySet()) {
//                    String fieldName = entry.getKey();
//                    Object value = entry.getValue();
//
//                    Field field;
//                    try {
//                        field  = clazz.getDeclaredField(fieldName);
//                        if(value == null){
//                            value = CommUtils.getDefaultVal(field.getType().getName());
//                        }
//
//                        //表示这个属性(字段)允许访问(设置值)
//                        field.setAccessible(true);
//                        field.set(entity, value);
//                    }catch (NoSuchFieldException ignored){
//                    }catch (IllegalArgumentException e){ throw e; }
//                }
//                list.add(entity);
//            }
//        }catch (SQLException e){
//            logger.info(
//                    "\nsql error\n===================\nSQL ==>\n {}\n===================\nparams = {}\n"
//                    , sql, Arrays.toString(params));
//            throw e;
//        }
//        return list;
//   }

   /**
    * 通用查询（Collection）
    */
   <T> List<T> query(Class<T> clazz, String sql, Object... params) throws Exception {
       Map<String, Object> map;
       List<T> list = new ArrayList<>();
       try{
           executeAll(false, sql, params);
           resultSet = statement.executeQuery();
           ResultSetMetaData metaData = resultSet.getMetaData();
           while (resultSet.next()) {
               map = new HashMap<>();
               getResultMap(map, metaData);
               list.add(JSONObject.parseObject(JSONObject.toJSONString(map), clazz));
           }
       }catch (SQLException e){
           sqlPrint(sql, params);
           throw e;
       }
       return list;

   }

   /**
    * 获取结果集对象
    */
    private void getResultMap(Map<String, Object> map, ResultSetMetaData metaData) throws SQLException {
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnLabel(i + 1);
            Object object = resultSet.getObject(i + 1);
            map.put(dbCustomStrategy.isUnderlineToCamel() ? CommUtils.underlineToCamel(columnName) : columnName, object);
        }
    }


    /**
     * Count(1) SQL
     */
   long executeSql(String sql, Object... params) throws Exception {
        long result = 0;
        try {
            executeAll(false, sql,params);
            resultSet = statement.executeQuery();
            if (resultSet.next()){
                result = (long) resultSet.getObject(1);
            }
        }catch (SQLException e){
            sqlPrint(sql, params);
            throw e;
        }
        return result;
   }

    /**
     * 通用查询sql
     */
   <T> T executeSql(Class<T> t, String sql, Object... params) throws Exception {
       Map<String, Object> map = new HashMap<>();
        try {
            executeAll(false, sql,params);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            if (resultSet.next()){
                getResultMap(map, metaData);
            }
        }catch (SQLException e){
            sqlPrint(sql, params);
            throw e;

        }
        return JSONObject.parseObject(JSONObject.toJSONString(map), t);
   }


    /**
     * 通用删 /改
     */
   int executeUpdate(String sql, Object... params) throws Exception{
        int res = 0;
        try{
            executeAll(false, sql, params);
            res = statement.executeUpdate();
        }catch (SQLException e){
            sqlPrint(sql, params);
            throw e;
        }
       return res;
   }

    /**
     * 插入
     */
    @SuppressWarnings("Unchecked")
   <T> int executeInsert(List<T> obj, String sql, String keyField, Object... params) throws Exception {
        int res = 0;
        try{
            executeAll(true, sql, params);
            res = statement.executeUpdate();
        }catch (SQLException e){
            sqlPrint(sql, params);
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
            if(type.equals(Long.class) || type.equals(long.class)) {
                long key = Long.parseLong(resultSet.getObject( 1).toString());
                writeMethod.invoke(t, key);
            }else if(type.equals(Integer.class) || type.equals(int.class)) {
                int key = Integer.parseInt(resultSet.getObject( 1).toString());
                writeMethod.invoke(t, key);
            }
            count++;
        }
        return res;
   }

    /**
     * 执行表结构创建或删除
     */
    void executeTableSql(String sql) throws SQLException {
        statement = conn.prepareStatement(sql);
        statement.execute();
    }

    /**
     * 查询表是否存在
     */
    long executeTableExist(String sql) throws SQLException {
        long count = 0;
        statement =  conn.prepareStatement(sql);
        resultSet = statement.executeQuery();
        while (resultSet.next()) {
            count = (long) resultSet.getObject(SymbolConst.DEFAULT_ONE);
        }
        return count;
    }







}
