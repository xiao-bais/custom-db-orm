package com.custom.jdbc;

import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.GlobalConst;
import com.custom.exceptions.CustomCheckException;
import com.custom.utils.DbPageRows;
import com.custom.utils.JudgeUtilsAx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2021/7/4
 * @Description
 */
public class JdbcTableDao {

    private Logger logger = LoggerFactory.getLogger(JdbcTableDao.class);

    private JdbcUtils jdbcUtils;
    private DbParserFieldHandler dbParserFieldHandler;

    public JdbcTableDao(DbDataSource dbDataSource){
        dbParserFieldHandler = new DbParserFieldHandler();
        jdbcUtils = new JdbcUtils(dbDataSource, dbParserFieldHandler);
    }


    /* ----------------------------------------------------------------select---------------------------------------------------------------- */
    /**
     * 查询全部
     */
   <T> List<T> selectList(Class<T> t, String condition, String orderBy, Object... params) throws Exception {
       if (JudgeUtilsAx.isNotEmpty(condition)) {
           condition = String.format("where 1 = 1 %s", condition);
       }
       String selectSql = String.format("%s %s %s", dbParserFieldHandler.getSelectSql(t), JudgeUtilsAx.isNotEmpty(condition) ? condition : "",
               JudgeUtilsAx.isNotEmpty(orderBy) ? orderBy : "");
       return jdbcUtils.query(t, selectSql, params);
   }

   /**
    * 分页查询1
    */
   <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, String orderBy, int pageIndex, int pageSize, Object... params) throws Exception {
        if (JudgeUtilsAx.isNotEmpty(condition)) {
            condition = String.format("where 1 = 1 %s", condition);
        }
        if(JudgeUtilsAx.isNotEmpty(orderBy)){
            orderBy = String.format("\norder by %s", orderBy);
        }
        String selectSql = String.format("%s %s %s", dbParserFieldHandler.getSelectSql(t), JudgeUtilsAx.isNotEmpty(condition) ? condition : "",
                JudgeUtilsAx.isNotEmpty(orderBy) ? orderBy :  "");
        String countSql = String.format("select count(0) from (%s) xxx ", selectSql);
        selectSql = String.format("%s \nlimit %s, %s", selectSql, (pageIndex - 1) * pageSize, pageSize);
        List<T> dataList = jdbcUtils.query(t, selectSql, params);
        DbPageRows<T> dbPageRows = new DbPageRows<>(pageIndex, pageSize, jdbcUtils.executeSql(countSql, params));
        dbPageRows.setData(dataList);
        dbPageRows.setCondition(condition);
        return dbPageRows;
    }

    /**
     * 分页查询2
     */
   <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, DbPageRows<T> dbPageRows, String orderBy,  Object... params) throws Exception {
        if (JudgeUtilsAx.isNotEmpty(condition)) {
            condition = String.format("where 1 = 1 %s", condition);
        }
        if(JudgeUtilsAx.isNotEmpty(orderBy)){
            orderBy = String.format("\norder by %s", orderBy);
        }
        if(dbPageRows == null){
            dbPageRows = new DbPageRows<>();
        }
        String selectSql = String.format("%s %s %s", dbParserFieldHandler.getSelectSql(t), JudgeUtilsAx.isNotEmpty(condition) ? condition : "",
                JudgeUtilsAx.isNotEmpty(orderBy) ? orderBy : "");
        String countSql = String.format("select count(0) from (%s) xxx ", selectSql);
        selectSql = String.format("%s \nlimit %s, %s", selectSql, (dbPageRows.getPageIndex() - 1) * dbPageRows.getPageSize(), dbPageRows.getPageSize());
        dbPageRows.setTotal(jdbcUtils.executeSql(countSql, params));
        List<T> dataList = jdbcUtils.query(t, selectSql, params);
        dbPageRows.setData(dataList);
        dbPageRows.setCondition(condition);
        return dbPageRows;
   }

    /**
     * 根据主键获取一条记录
     */
   <T> T selectOneByKey(Class<T> t, Object key) throws Exception {
        if(key == null) throw new NullPointerException();
        String alias = dbParserFieldHandler.getDbTableAlias(t);
        String selectSql = String.format("%s \nwhere %s.`%s` = ?", dbParserFieldHandler.getSelectSql(t), alias, dbParserFieldHandler.getDbFieldKey(t));
        return jdbcUtils.executeSql(t, selectSql, key);
   }

    /**
     * 根据条件获取一条记录
     */
    <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception {
        if (JudgeUtilsAx.isNotEmpty(condition)) {
            condition = String.format("where 1 = 1 %s", condition);
        }
        String selectSql = String.format("%s \n%s", dbParserFieldHandler.getSelectSql(t), condition);
        return jdbcUtils.executeSql(t, selectSql, params);
    }

    /**
     * 纯sql查询1
     */
    <T> List<T> selectBySql(Class<T> t, String sql, Object... params) throws Exception {
        if(JudgeUtilsAx.isEmpty(sql)) {
            throw new CustomCheckException(GlobalConst.EX_SQL_NOT_EMPTY);
        }
        return jdbcUtils.query(t, sql, params);
    }

    /**
     * 纯sql查询2
     */
    <T> T selectOneBySql(Class<T> t, String sql, Object... params) throws Exception {
        if(JudgeUtilsAx.isEmpty(sql)) {
            throw new CustomCheckException(GlobalConst.EX_SQL_NOT_EMPTY);
        }
        List<T> queryList = jdbcUtils.query(t, sql, params);
        if(queryList.size() == 0){
            return null;
        }else if(queryList.size() > 1){
            throw new CustomCheckException(String.format(GlobalConst.EX_QUERY_MORE_RESULT, queryList.size()));
        }
        return queryList.get(0);
    }


    /* ----------------------------------------------------------------delete---------------------------------------------------------------- */
    /**
     * 根据主键删除
     */
    <T> int deleteByKey(Class<T> t, Object key) throws Exception {
        if(key == null) return 0;
        String alias = dbParserFieldHandler.getDbTableAlias(t);
        String deleteSql = String.format("delete from %s %s where %s.`%s` = ?",
                dbParserFieldHandler.getDbTableName(t), alias, alias, dbParserFieldHandler.getDbFieldKey(t));
        return jdbcUtils.executeUpdate(deleteSql, key);
    }

    /**
     * 根据主键批量删除
     */
    <T> int deleteBatchKeys(Class<T> t, Object[] keys) throws Exception {
        if(keys == null) return 0;
        StringJoiner delSymbols = new StringJoiner(",", "(", ")");
        int symbol = keys.length;
        do {
            delSymbols.add("?");
            symbol--;
        }while (symbol > 0);
        String alias = dbParserFieldHandler.getDbTableAlias(t);
        String deleteSql = String.format("delete from %s %s where %s.`%s` in %s",
                dbParserFieldHandler.getDbTableName(t), alias, alias, dbParserFieldHandler.getDbFieldKey(t), delSymbols);
        return jdbcUtils.executeUpdate(deleteSql, keys);
    }

    /**
     * 根据条件删除
     */
    <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception {
        if(JudgeUtilsAx.isEmpty(condition)){
            throw new RuntimeException(GlobalConst.EX_DEL_CONDITION_NOT_EMPTY);
        }
        condition = String.format("where 1 = 1 %s", condition);
        String alias = dbParserFieldHandler.getDbTableAlias(t);
        String deleteSql = String.format("delete from %s %s %s", dbParserFieldHandler.getDbTableName(t), alias, condition);
        return jdbcUtils.executeUpdate(deleteSql, params);
    }

    /* ----------------------------------------------------------------insert---------------------------------------------------------------- */
    /**
     * 插入一条记录
     */
    <T> int insert(T t) throws Exception {
        //数据库字段
        String[] dbFields = dbParserFieldHandler.getDbFields(t.getClass());
        //java属性值
        List<Object> fieldsVal = dbParserFieldHandler.getFieldsVal(t, dbParserFieldHandler.getFiledNames(t.getClass()));
        StringJoiner dbFieldStr = new StringJoiner(", ", "(", ")");

        //如果存在主键
        if(dbParserFieldHandler.isDbKeyTag(t.getClass())){
            //主键字段
            String dbFieldKey = dbParserFieldHandler.getDbFieldKey(t.getClass());
            //主键值
            Object fieldKeyVal = dbParserFieldHandler.getFieldValue(t, dbParserFieldHandler.getFieldKey(t.getClass()));
            dbFieldStr.add(String.format("`%s`", dbFieldKey));
            fieldsVal.add(0, fieldKeyVal);
        }
        Arrays.stream(dbFields).map(dbField -> String.format("`%s`", dbField)).forEach(dbFieldStr::add);

        /* 拼接预编译的? */
        StringJoiner insertSymbol = new StringJoiner(", ","(",")");
        int symbol = fieldsVal.size();
        do {
            insertSymbol.add("?");
            symbol--;
        }while (symbol > 0);
        String insertSql = String.format("insert into `%s` %s values %s",
                dbParserFieldHandler.getDbTableName(t.getClass()), dbFieldStr, insertSymbol);
        return jdbcUtils.executeUpdate(insertSql, fieldsVal.toArray());
    }

    /**
     * 批量插入记录
     */
    <T> int insert(List<T> tList) throws Exception {
        if(null == tList) {
            throw new RuntimeException(GlobalConst.EX_PARAM_EMPTY);
        }
        T t = tList.get(0);
        //数据库字段
        String[] dbFields = dbParserFieldHandler.getDbFields(t.getClass());
        StringJoiner dbFieldStr = new StringJoiner(", ", "(", ")");

        //如果存在主键
        boolean existKey = dbParserFieldHandler.isDbKeyTag(t.getClass());
        if(existKey){
            //主键字段
            String dbFieldKey = dbParserFieldHandler.getDbFieldKey(t.getClass());
            if (!dbFieldStr.toString().contains(dbFieldKey)) {
                dbFieldStr.add(dbFieldKey);
            }
        }
        Arrays.stream(dbFields).forEach(dbFieldStr::add);
        StringJoiner inertValStr = new StringJoiner(",");
        List<Object> saveValues = new ArrayList<>();
        //java属性值
        for (T obj : tList) {
            List<Object> fieldsVal = dbParserFieldHandler.getFieldsVal(obj, dbParserFieldHandler.getFiledNames(t.getClass()));
            if(existKey){
                fieldsVal.add(0, dbParserFieldHandler.generateKey(obj));
            }
            saveValues.addAll(fieldsVal);
            /* 拼接预编译的? */
            StringJoiner insertSymbol = new StringJoiner(", ","(",")");
            int symbol = fieldsVal.size();
            do {
                insertSymbol.add("?");
                symbol--;
            }while (symbol > 0);
            inertValStr.add("\n"+insertSymbol.toString());
        }
        String insertSql = String.format("insert into `%s` %s values %s",
               dbParserFieldHandler.getDbTableName(t.getClass()), dbFieldStr, inertValStr);
        return jdbcUtils.executeUpdate(insertSql, saveValues.toArray());
    }

    /* ----------------------------------------------------------------update---------------------------------------------------------------- */
    /**
     * 根据指定字段修改一条记录
     */
    <T> int updateByKey(T t, String... updateDbFields) throws Exception {

        StringJoiner editSymbol = new StringJoiner(",");
        List<String> updateDbColumns = new ArrayList<>();
        List<Object> updateDbValues = new ArrayList<>();

        if(updateDbFields == null || updateDbFields.length == 0){
            List<String> dbFields =  Arrays.stream(dbParserFieldHandler.getDbFields(t.getClass())).collect(Collectors.toList());
            List<Object> fieldsVal = dbParserFieldHandler.getFieldsVal(t, dbParserFieldHandler.getFiledNames(t.getClass()));
            for (int i = 0; i < dbFields.size(); i++) {
                if(JudgeUtilsAx.isEmpty(fieldsVal.get(i))) {
                    continue;
                }
                updateDbColumns.add(dbFields.get(i));
                updateDbValues.add(fieldsVal.get(i));
            }
        }else {
            updateDbColumns = Arrays.stream(updateDbFields).collect(Collectors.toList());
            for (String column : updateDbColumns)
                updateDbValues.add(dbParserFieldHandler.getFieldValue(t, dbParserFieldHandler.getProFieldName(t.getClass(), column)));
        }

        for (int i = 0; i < updateDbColumns.size(); i++) {
            editSymbol.add(String.format(" %s = ?", updateDbColumns.get(i)));
        }
        String dbTableName = dbParserFieldHandler.getDbTableName(t.getClass());
        String fieldKey = dbParserFieldHandler.getDbFieldKey(t.getClass());
        updateDbValues.add(dbParserFieldHandler.getFieldValue(t, dbParserFieldHandler.getProFieldName(t.getClass(), fieldKey)));
        String updateSql = String.format(" update %s set %s where %s = ?", dbTableName, editSymbol, fieldKey);
        return jdbcUtils.executeUpdate(updateSql, updateDbValues.toArray());
    }

    /* ----------------------------------------------------------------common---------------------------------------------------------------- */
    /**
     * 保存（更新或插入）
     */
    <T> int save(T t) throws Exception {
        int update = updateByKey(t);
        if(update == 0) {
            update = insert(t);
        }
        return update;
    }







}