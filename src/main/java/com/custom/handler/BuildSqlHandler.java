package com.custom.handler;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.SymbolConst;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;
import com.custom.page.DbPageRows;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2021/7/4
 * @Description
 */
public class BuildSqlHandler {

    private SqlExecuteHandler sqlExecuteHandler;
    private DbParserFieldHandler dbParserFieldHandler;
    private String logicDeleteUpdateSql = SymbolConst.EMPTY;
    private String logicDeleteQuerySql = SymbolConst.EMPTY;

    public BuildSqlHandler(DbDataSource dbDataSource){
        dbParserFieldHandler = new DbParserFieldHandler();
        sqlExecuteHandler = new SqlExecuteHandler(dbDataSource, dbParserFieldHandler);
        DbCustomStrategy dbCustomStrategy = dbDataSource.getDbCustomStrategy();
        initLogic(dbCustomStrategy);
    }


    /**
     * 初始化逻辑删除的sql
     */
    public void initLogic(DbCustomStrategy dbCustomStrategy) {

        if(JudgeUtilsAx.isLogicDeleteOpen(dbCustomStrategy)) {
            if(JudgeUtilsAx.isEmpty(dbCustomStrategy.getNotDeleteLogicValue())
                    || JudgeUtilsAx.isEmpty(dbCustomStrategy.getDeleteLogicValue())) {
                throw new CustomCheckException(ExceptionConst.EX_LOGIC_EMPTY_VALUE);
            }

            this.logicDeleteUpdateSql = String.format("`%s` = %s ", dbCustomStrategy.getDbFieldDeleteLogic(), dbCustomStrategy.getDeleteLogicValue());
            this.logicDeleteQuerySql = String.format("`%s` = %s ", dbCustomStrategy.getDbFieldDeleteLogic(), dbCustomStrategy.getNotDeleteLogicValue());
        }
    }


    /* ----------------------------------------------------------------select---------------------------------------------------------------- */
    /**
     * 查询全部
     */
   <T> List<T> selectList(Class<T> t, String condition, String orderBy, Object... params) throws Exception {

       condition = dbParserFieldHandler.checkConditionAndLogicDeleteSql(t, condition, logicDeleteQuerySql);
       JudgeUtilsAx.checkObjNotNull(t);
       String selectSql = String.format("%s %s %s", dbParserFieldHandler.getSelectSql(t), JudgeUtilsAx.isNotEmpty(condition) ? condition : "",
               JudgeUtilsAx.isNotEmpty(orderBy) ? orderBy : SymbolConst.EMPTY);
       return sqlExecuteHandler.query(t, selectSql, params);
   }

   /**
    * 分页查询1
    */
   <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, String orderBy, int pageIndex, int pageSize, Object... params) throws Exception {

       condition = dbParserFieldHandler.checkConditionAndLogicDeleteSql(t, condition, logicDeleteQuerySql);
        JudgeUtilsAx.checkObjNotNull(t);
        if(JudgeUtilsAx.isNotEmpty(orderBy)){
            orderBy = String.format("\norder by %s", orderBy);
        }
        String selectSql = String.format("%s %s %s", dbParserFieldHandler.getSelectSql(t), JudgeUtilsAx.isNotEmpty(condition) ? condition : "",
                JudgeUtilsAx.isNotEmpty(orderBy) ? orderBy :  SymbolConst.EMPTY);
        String countSql = String.format("select count(0) from (%s) xxx ", selectSql);

        List<T> dataList = new ArrayList<>();
        long count = (long) sqlExecuteHandler.selectOneSql(countSql, params);
        if(count > 0) {
            selectSql = String.format("%s \nlimit %s, %s", selectSql, (pageIndex - 1) * pageSize, pageSize);
            dataList = sqlExecuteHandler.query(t, selectSql, params);
        }

        DbPageRows<T> dbPageRows = new DbPageRows<>(pageIndex, pageSize, count);
        dbPageRows.setData(dataList);
        dbPageRows.setCondition(condition);
        return dbPageRows;
    }

    /**
     * 分页查询2
     */
   <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, String orderBy, DbPageRows<T> dbPageRows, Object... params) throws Exception {
       if(dbPageRows == null) {
           dbPageRows = new DbPageRows<>();
       }
       return selectPageRows(t, condition, orderBy, dbPageRows.getPageIndex(), dbPageRows.getPageSize(), params);
   }

    /**
     * 根据主键获取一条记录
     */
   <T> T selectOneByKey(Class<T> t, Object key) throws Exception {
        if(key == null) throw new NullPointerException();
        JudgeUtilsAx.checkObjNotNull(t);
        String alias = dbParserFieldHandler.getDbTableAlias(t);
        String selectSql = String.format("%s \nwhere %s.`%s` = ?", dbParserFieldHandler.getSelectSql(t), alias, dbParserFieldHandler.getDbFieldKey(t));
        return sqlExecuteHandler.selectOneSql(t, selectSql, key);
   }

   /**
   * 根据多个主键获取多条记录
   */

   <T> List<T> selectBatchByKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception {
       if(keys == null || keys.size() == 0) throw new NullPointerException();
       JudgeUtilsAx.checkObjNotNull(t);
       String alias = dbParserFieldHandler.getDbTableAlias(t);
       StringJoiner symbolKeys = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
       for (int i = 0; i < keys.size(); i++) {
           symbolKeys.add(SymbolConst.QUEST);
       }
       String selectSql = String.format("%s \nwhere %s.`%s` in (%s) ", dbParserFieldHandler.getSelectSql(t), alias, dbParserFieldHandler.getDbFieldKey(t), symbolKeys);
       return sqlExecuteHandler.query(t, selectSql, keys.toArray());
   }

    /**
     * 根据条件获取一条记录
     */
    <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception {
        condition = dbParserFieldHandler.checkConditionAndLogicDeleteSql(t, condition, logicDeleteQuerySql);
        JudgeUtilsAx.checkObjNotNull(t);
        String selectSql = String.format("%s %s", dbParserFieldHandler.getSelectSql(t), condition);
        return sqlExecuteHandler.selectOneSql(t, selectSql, params);
    }

    /**
     * 纯sql查询1
     */
    <T> List<T> selectBySql(Class<T> t, String sql, Object... params) throws Exception {
        if(JudgeUtilsAx.isEmpty(sql)) {
            throw new CustomCheckException(ExceptionConst.EX_SQL_NOT_EMPTY);
        }
        JudgeUtilsAx.checkObjNotNull(t);
        return sqlExecuteHandler.query(t, sql, params);
    }

    /**
     * 纯sql查询2
     */
    <T> T selectOneBySql(Class<T> t, String sql, Object... params) throws Exception {
        if(JudgeUtilsAx.isEmpty(sql)) {
            throw new CustomCheckException(ExceptionConst.EX_SQL_NOT_EMPTY);
        }
        JudgeUtilsAx.checkObjNotNull(t);
        List<T> queryList = sqlExecuteHandler.query(t, sql, params);
        if(queryList.size() == 0){
            return null;
        }else if(queryList.size() > 1){
            throw new CustomCheckException(String.format(ExceptionConst.EX_QUERY_MORE_RESULT, queryList.size()));
        }
        return queryList.get(0);
    }

    /**
    * 查询单个值的纯sql
    */
    Object selectObjBySql(String sql, Object... params) throws Exception {
        if(JudgeUtilsAx.isEmpty(sql)) {
            throw new CustomCheckException(ExceptionConst.EX_SQL_NOT_EMPTY);
        }
        return sqlExecuteHandler.selectOneSql(sql, params);
    }


    /* ----------------------------------------------------------------delete---------------------------------------------------------------- */
    /**
     * 根据主键删除
     */
    <T> int deleteByKey(Class<T> t, Object key) throws Exception {
        if(key == null) return 0;
        JudgeUtilsAx.checkObjNotNull(t);
        String deleteSql = dbParserFieldHandler.getDeleteSql(t, logicDeleteUpdateSql, SymbolConst.QUEST, false);
        return sqlExecuteHandler.executeUpdate(deleteSql, key);
    }

    /**
     * 根据主键批量删除
     */
    <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception {
        if(keys == null || keys.size() == 0) return 0;
        JudgeUtilsAx.checkObjNotNull(t);
        StringJoiner delSymbols = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        for (int i = 0; i < keys.size(); i++) {
            delSymbols.add(SymbolConst.QUEST);
        }
        String deleteSql = dbParserFieldHandler.getDeleteSql(t, logicDeleteUpdateSql, String.format("(%s)", delSymbols), true);
        return sqlExecuteHandler.executeUpdate(deleteSql, keys.toArray());
    }

    /**
     * 根据条件删除
     */
    <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception {
        if(JudgeUtilsAx.isEmpty(condition)){
            throw new RuntimeException(ExceptionConst.EX_DEL_CONDITION_NOT_EMPTY);
        }
        JudgeUtilsAx.checkObjNotNull(t);
        condition = String.format("where 1 = 1 %s", condition);
        String alias = dbParserFieldHandler.getDbTableAlias(t);
        String deleteSql = String.format("delete from %s %s %s", dbParserFieldHandler.getDbTableName(t), alias, condition);
        return sqlExecuteHandler.executeUpdate(deleteSql, params);
    }

    /* ----------------------------------------------------------------insert---------------------------------------------------------------- */
    /**
     * 插入一条记录
     */
    <T> int insert(T t, boolean isGeneratedKey) throws Exception {
        JudgeUtilsAx.checkObjNotNull(t.getClass());
        //数据库字段
        String[] dbFields = dbParserFieldHandler.getDbFields(t.getClass());
        //java属性值
        List<Object> fieldsVal = dbParserFieldHandler.getFieldsVal(t, dbParserFieldHandler.getFiledNames(t.getClass()));
        StringJoiner dbFieldStr = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2, SymbolConst.BRACKETS_LEFT, SymbolConst.BRACKETS_RIGHT);

        //如果存在主键
        String dbFieldKey = SymbolConst.EMPTY;
        if(dbParserFieldHandler.isDbKeyTag(t.getClass())){
            //主键字段
            dbFieldKey = dbParserFieldHandler.getDbFieldKey(t.getClass());
            //主键值
            Object fieldKeyVal = dbParserFieldHandler.getFieldValue(t, dbParserFieldHandler.getFieldKey(t.getClass()));
            dbFieldStr.add(String.format("`%s`", dbFieldKey));
            fieldsVal.add(0, fieldKeyVal);
        }
        Arrays.stream(dbFields).map(dbField -> String.format("`%s`", dbField)).forEach(dbFieldStr::add);

        /* 拼接预编译的? */
        StringJoiner insertSymbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2, SymbolConst.BRACKETS_LEFT, SymbolConst.BRACKETS_RIGHT);
        int symbol = fieldsVal.size();
        do {
            insertSymbol.add(SymbolConst.QUEST);
            symbol--;
        }while (symbol > 0);
        String insertSql = String.format("insert into `%s` %s values %s",
                dbParserFieldHandler.getDbTableName(t.getClass()), dbFieldStr, insertSymbol);
        return isGeneratedKey ?
                sqlExecuteHandler.executeInsert(Collections.singletonList(t), insertSql, dbFieldKey, fieldsVal.toArray()) :
                sqlExecuteHandler.executeUpdate(insertSql, fieldsVal.toArray());
    }

    /**
     * 批量插入记录
     */
    <T> int insert(List<T> tList, boolean isGeneratedKey) throws Exception {
        if(null == tList) {
            throw new RuntimeException(ExceptionConst.EX_PARAM_EMPTY);
        }
        T t = tList.get(0);
        //数据库字段
        String[] dbFields = dbParserFieldHandler.getDbFields(t.getClass());
        StringJoiner dbFieldStr = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2, SymbolConst.BRACKETS_LEFT, SymbolConst.BRACKETS_RIGHT);

        //如果存在主键
        boolean existKey = dbParserFieldHandler.isDbKeyTag(t.getClass());
        String dbFieldKey = SymbolConst.EMPTY;
        if(existKey){
            //主键字段
            dbFieldKey = dbParserFieldHandler.getDbFieldKey(t.getClass());
            if (!dbFieldStr.toString().contains(dbFieldKey)) {
                dbFieldStr.add(String.format("`%s`", dbFieldKey));
            }
        }
        Arrays.stream(dbFields).forEach(x -> dbFieldStr.add(String.format("`%s`", x)));
        StringJoiner inertValStr = new StringJoiner(SymbolConst.SEPARATOR_COMMA_1);
        List<Object> saveValues = new ArrayList<>();
        //java属性值
        for (T obj : tList) {
            List<Object> fieldsVal = dbParserFieldHandler.getFieldsVal(obj, dbParserFieldHandler.getFiledNames(t.getClass()));
            if(existKey){
                fieldsVal.add(0, dbParserFieldHandler.generateKey(obj));
            }
            saveValues.addAll(fieldsVal);
            /* 拼接预编译的? */
            StringJoiner insertSymbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2, SymbolConst.BRACKETS_LEFT, SymbolConst.BRACKETS_RIGHT);
            int symbol = fieldsVal.size();
            do {
                insertSymbol.add(SymbolConst.QUEST);
                symbol--;
            }while (symbol > 0);
            inertValStr.add("\n" + insertSymbol.toString());
        }
        String insertSql = String.format("insert into `%s` %s values %s",
               dbParserFieldHandler.getDbTableName(t.getClass()), dbFieldStr, inertValStr);
        return isGeneratedKey ?
                sqlExecuteHandler.executeInsert(tList, insertSql, dbFieldKey, saveValues.toArray()) :
                sqlExecuteHandler.executeUpdate(insertSql, saveValues.toArray());
    }

    /* ----------------------------------------------------------------update---------------------------------------------------------------- */
    /**
     * 根据指定字段修改一条记录
     */
    <T> int updateByKey(T t, String... updateDbFields) throws Exception {
        JudgeUtilsAx.checkObjNotNull(t.getClass());
        if(null == dbParserFieldHandler.getFieldKeyType(t.getClass())) {
            throw new CustomCheckException(ExceptionConst.EX_DBKEY_NOTFOUND);
        }
        StringJoiner editSymbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_1);
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

        for (String updateDbColumn : updateDbColumns) {
            editSymbol.add(String.format(" `%s` = ?", updateDbColumn));
        }
        String dbTableName = dbParserFieldHandler.getDbTableName(t.getClass());
        String fieldKey = dbParserFieldHandler.getDbFieldKey(t.getClass());
        updateDbValues.add(dbParserFieldHandler.getFieldValue(t, dbParserFieldHandler.getProFieldName(t.getClass(), fieldKey)));
        String updateSql = String.format(" update %s set %s where %s = ?", dbTableName, editSymbol, fieldKey);
        return sqlExecuteHandler.executeUpdate(updateSql, updateDbValues.toArray());
    }

    /* ----------------------------------------------------------------common---------------------------------------------------------------- */
    /**
     * 保存（更新或插入）
     */
    <T> long save(T t) throws Exception {
        if(!CustomUtil.isKeyTag(t.getClass())){
            throw new CustomCheckException(ExceptionConst.EX_DBKEY_NOTFOUND + t);
        }
        long update = updateByKey(t);
        if(update == 0) {
            update = insert(t, false);
        }
        return update;
    }

    /**
    * 执行一条sql
    */
    int executeSql(String sql, Object... params) throws Exception {
        if(JudgeUtilsAx.isEmpty(sql)){
            throw new NullPointerException();
        }
        return sqlExecuteHandler.executeUpdate(sql, params);
    }







}
