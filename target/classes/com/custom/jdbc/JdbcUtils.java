package com.custom.jdbc;

import com.custom.dbconfig.DbConnection;
import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.GlobalConst;
import com.custom.exceptions.CustomCheckException;
import com.custom.utils.CommUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/9 0009 3:11
 * @Version 1.0
 * @Description JdbcUtils
 */
public class JdbcUtils extends DbConnection {

    private Logger logger = LoggerFactory.getLogger(JdbcUtils.class);


    private Connection conn = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private DbParserFieldHandler parserFieldHandler;

    public JdbcUtils(DbDataSource dbDataSource, DbParserFieldHandler parserFieldHandler){
        super(dbDataSource);
        conn = super.getConnection();
        this.parserFieldHandler = parserFieldHandler;
    }

    private void executeAll(boolean isSave,String sql, Object... params) throws Exception {
        statement = isSave ? conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) : conn.prepareStatement(sql);
        if(params.length <= 0) return;
        for (int i = 0; i < params.length; i++) {
            statement.setObject((i + 1), params[i]);
        }
    }

    /**
     * 通用查询
     */
   public <T> List<T> query(Class<T> clazz, String sql, Object... params) throws Exception{
        Map<String,Object> map = null;
        List<T> list = new ArrayList<>();
        T entity = null;
        map = new HashMap<>();
        try{
            executeAll(false,sql, params);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                map.clear();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    String columnName = metaData.getColumnName(i + 1);
                    Object object = resultSet.getObject(i + 1);
                    map.put(parserFieldHandler.getProFieldName(clazz, columnName), object);
                }
                if (map.size() <= 0)  continue;
                try{
                    //利用反射去实例化对象
                    entity = clazz.newInstance();
                }catch (InstantiationException  e){
                    logger.error(e.toString(), e);

                }
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String fieldName = entry.getKey();
                    Object value = entry.getValue();

                    Field field = null;
                    try {
                        field  = clazz.getDeclaredField(fieldName);
                        if(value == null){
                            value = CommUtils.getDefaultVal(field.getType().getName());
                        }
                        //表示这个属性(字段)允许访问(设置值)
                        field.setAccessible(true);
                        field.set(entity, value);
                    }catch (NoSuchFieldException ignored){
                    }catch (IllegalArgumentException e){
                        logger.error(e.toString(), e);
                    }
                }
                list.add(entity);

            }
        }catch (SQLException e){
            logger.error(e.toString(), e);
            logger.info(
                    "\nsql error\n===================\nSQL ==>\n {}\n===================\nparams = {}\n"
                    , sql, Arrays.toString(params));
        }
        return list;
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
            logger.error(e.toString(), e);
            logger.info(
                    "\nsql error\n===================\nSQL ==>\n {}\n===================\nparams = {}\n"
                    , sql, Arrays.toString(params));
        }
        return result;
   }

    /**
     * 通用查询sql
     */
   <T> T executeSql(Class<T> t, String sql, Object... params) throws Exception {
        T entity = null;
        try {
            executeAll(false, sql,params);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            if (resultSet.next()){
                entity = t.newInstance();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    Field field = null;
                    String columnName = metaData.getColumnName(i + 1);
                    Object value = resultSet.getObject(i + 1);
                    field  = t.getDeclaredField(columnName);
                    if(value == null){
                        value = CommUtils.getDefaultVal(field.getType().getName());
                    }
                    //表示这个属性(字段)允许访问(设置值)
                    field.setAccessible(true);
                    field.set(entity, value);
                }
            }
        }catch (SQLException e){
            logger.error(e.toString(), e);
            logger.info(
                    "\nsql error\n===================\nSQL ==>\n {}\n===================\nparams = {}\n"
                    , sql, Arrays.toString(params));
        } catch (IllegalAccessException | InstantiationException | NoSuchFieldException e) {
            logger.error(e.toString(), e);
        }
        return entity;
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
            logger.error(e.toString(), e);
            logger.info(
                    "\nsql error\n===================\nSQL ==>\n {}\n===================\nparams = {}\n"
                    , sql, Arrays.toString(params));
        } catch (Exception e){
            logger.error(e.toString(), e);
        }
        return res;
   }

    /**
     * 插入
     */
    @SuppressWarnings("Unchecked")
   <T> int executeUpdate(List<T> obj, String keyField, Class<?> type,  String sql, Object... params) {
       int res = 0;
        try{
            executeAll(true, sql, params);
            res = statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();
            int count = 0;
            while (resultSet.next()){
                count++;
                Object key =  resultSet.getObject( 1);
                Method method = obj.get(count).getClass().getMethod(initSetStr(keyField), type);
                method.invoke(obj.get(count), key);
            }
        }catch (SQLException e){
            logger.error(e.toString(), e);
            logger.info(
                    "\nsql error\n===================\nSQL ==>\n {}\n===================\nparams = {}\n"
                    , sql, Arrays.toString(params));
        } catch (Exception e){
            logger.error(e.toString(), e);
        }
        return res;
   }

    // 将单词的首字母大写
    public static String initSetStr(String old){
        return String.format("set%s", old.substring(0,1).toUpperCase() + old.substring(1));
    }



}
