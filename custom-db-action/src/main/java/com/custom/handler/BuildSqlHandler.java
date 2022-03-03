package com.custom.handler;

import com.custom.annotations.check.CheckExecute;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbaction.AbstractSqlBuilder;
import com.custom.dbaction.SqlExecuteAction;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.ExecuteMethod;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;
import com.custom.comm.page.DbPageRows;
import com.custom.wrapper.AbstractWrapper;
import com.custom.wrapper.ConditionEntity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author Xiao-Bai
 * @Date 2021/7/4
 * @Description
 */
public class BuildSqlHandler extends AbstractSqlBuilder {

    private DbParserFieldHandler dbParserFieldHandler;
    private SqlExecuteAction sqlExecuteAction;
    private BuildTableHandler buildTableHandler;

    public BuildSqlHandler(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        this.dbParserFieldHandler = new DbParserFieldHandler();
        this.setSqlExecuteAction(new SqlExecuteAction(dbDataSource, dbCustomStrategy));
        this.sqlExecuteAction = getSqlExecuteAction();
        this.buildTableHandler = new BuildTableHandler(getSqlExecuteAction());
        this.setDbCustomStrategy(dbCustomStrategy);
        initLogic();
    }

    public BuildSqlHandler() {
    }


    /* ----------------------------------------------------------------select---------------------------------------------------------------- */

    /**
     * 查询全部
     */
    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectList(Class<T> t, String condition, String orderBy, Object... params) throws Exception {
        condition = dbParserFieldHandler.checkConditionAndLogicDeleteSql(t, condition, getLogicDeleteQuerySql());
        String selectSql = String.format("%s %s %s", dbParserFieldHandler.getSelectSql(t), JudgeUtilsAx.isNotEmpty(condition) ? condition : "",
                JudgeUtilsAx.isNotEmpty(orderBy) ? orderBy : SymbolConst.EMPTY);
        return sqlExecuteAction.query(t, selectSql, params);
    }

    /**
     * 分页查询1
     */
    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, String orderBy, int pageIndex, int pageSize, Object... params) throws Exception {

        condition = dbParserFieldHandler.checkConditionAndLogicDeleteSql(t, condition, getLogicDeleteQuerySql());
        if (JudgeUtilsAx.isNotEmpty(orderBy)) {
            orderBy = String.format("\norder by %s", orderBy);
        }
        String selectSql = String.format("%s %s %s", dbParserFieldHandler.getSelectSql(t), JudgeUtilsAx.isNotEmpty(condition) ? condition : "",
                JudgeUtilsAx.isNotEmpty(orderBy) ? orderBy : SymbolConst.EMPTY);
        String countSql = String.format("select count(0) from (%s) xxx ", selectSql);

        List<T> dataList = new ArrayList<>();
        long count = (long) sqlExecuteAction.selectOneSql(countSql, params);
        if (count > 0) {
            selectSql = String.format("%s \nlimit %s, %s", selectSql, (pageIndex - 1) * pageSize, pageSize);
            dataList = sqlExecuteAction.query(t, selectSql, params);
        }

        DbPageRows<T> dbPageRows = new DbPageRows<>(pageIndex, pageSize, count);
        dbPageRows.setData(dataList);
        dbPageRows.setCondition(condition);
        return dbPageRows;
    }

    /**
     * 分页查询2
     */
    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, String orderBy, DbPageRows<T> dbPageRows, Object... params) throws Exception {
        if (dbPageRows == null) {
            dbPageRows = new DbPageRows<>();
        }
        return selectPageRows(t, condition, orderBy, dbPageRows.getPageIndex(), dbPageRows.getPageSize(), params);
    }

    /**
     * 根据主键获取一条记录
     */
    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOneByKey(Class<T> t, Object key) throws Exception {
        String alias = dbParserFieldHandler.getDbTableAlias(t);
        String selectSql = String.format("%s \nwhere %s.`%s` = ?", dbParserFieldHandler.getSelectSql(t), alias, dbParserFieldHandler.getDbFieldKey(t));
        return sqlExecuteAction.selectOneSql(t, selectSql, key);
    }

    /**
     * 根据多个主键获取多条记录
     */
    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectBatchByKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception {
        String alias = dbParserFieldHandler.getDbTableAlias(t);
        StringJoiner symbolKeys = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        for (int i = 0; i < keys.size(); i++) {
            symbolKeys.add(SymbolConst.QUEST);
        }
        String selectSql = String.format("%s \nwhere %s.`%s` in (%s) ", dbParserFieldHandler.getSelectSql(t), alias, dbParserFieldHandler.getDbFieldKey(t), symbolKeys);
        return sqlExecuteAction.query(t, selectSql, keys.toArray());
    }

    /**
     * 根据条件获取一条记录
     */
    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception {
        condition = dbParserFieldHandler.checkConditionAndLogicDeleteSql(t, condition, getLogicDeleteQuerySql());
        String selectSql = String.format("%s %s", dbParserFieldHandler.getSelectSql(t), condition);
        return selectOneBySql(t, selectSql, params);
    }

    @Override
    public <T> DbPageRows<T> selectPageRows(Class<T> t, DbPageRows<T> dbPageRows, ConditionEntity<T> conditionEntity) throws Exception {
        return null;
    }

    @Override
    public <T> List<T> selectList(Class<T> t, AbstractWrapper<T, ?> conditionEntity) throws Exception {
        return null;
    }

    @Override
    public <T> T selectOneByCondition(ConditionEntity<T> conditionEntity) throws Exception {
        return null;
    }


    /* ----------------------------------------------------------------delete---------------------------------------------------------------- */

    /**
     * 根据主键删除
     */
    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByKey(Class<T> t, Object key) throws Exception {
        String[] deleteFieldArray = dbParserFieldHandler.getTableBaseFieldArray(t);
        String deleteSql = getLogicDeleteKeySql(SymbolConst.QUEST, deleteFieldArray[2], deleteFieldArray[0], deleteFieldArray[1], false);
        return sqlExecuteAction.executeUpdate(deleteSql, key);
    }

    /**
     * 根据主键批量删除
     */
    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception {
        StringJoiner delSymbols = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        IntStream.range(0, keys.size()).mapToObj(i -> SymbolConst.QUEST).forEach(delSymbols::add);
        String[] deleteFieldArray = dbParserFieldHandler.getTableBaseFieldArray(t);
        String deleteSql = getLogicDeleteKeySql(String.format("(%s)", delSymbols),
                deleteFieldArray[2], deleteFieldArray[0], deleteFieldArray[1], true);
        return sqlExecuteAction.executeUpdate(deleteSql, keys.toArray());
    }

    /**
     * 根据条件删除
     */
    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception {
        String deleteSql = dbParserFieldHandler.getDeleteSql(t, getLogicDeleteQuerySql(), getLogicDeleteUpdateSql(), condition);
        return sqlExecuteAction.executeUpdate(deleteSql, params);
    }

    @Override
    public <T> int deleteByCondition(Class<T> t, ConditionEntity<T> conditionEntity) throws Exception {
        return 0;
    }

    /* ----------------------------------------------------------------insert---------------------------------------------------------------- */

    /**
     * 插入一条记录
     */
    @Override
    @CheckExecute(target = ExecuteMethod.INSERT)
    public <T> int insert(T t, boolean isGeneratedKey) throws Exception {
        //数据库字段
        String[] dbFields = dbParserFieldHandler.getDbFields(t.getClass());
        //java属性值
        List<Object> fieldsVal = dbParserFieldHandler.getFieldsVal(t, dbParserFieldHandler.getFiledNames(t.getClass()));
        StringJoiner dbFieldStr = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2, SymbolConst.BRACKETS_LEFT, SymbolConst.BRACKETS_RIGHT);

        //如果存在主键
        String dbFieldKey = SymbolConst.EMPTY;
        if (dbParserFieldHandler.isDbKeyTag(t.getClass())) {
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
        } while (symbol > 0);
        String insertSql = String.format("insert into `%s` %s values %s",
                dbParserFieldHandler.getDbTableName(t.getClass()), dbFieldStr, insertSymbol);
        Field fieldKeyType = dbParserFieldHandler.getFieldKeyType(t.getClass());
        return isGeneratedKey ?
                sqlExecuteAction.executeInsert(Collections.singletonList(t), insertSql, dbFieldKey, fieldKeyType.getType(), fieldsVal.toArray()) :
                sqlExecuteAction.executeUpdate(insertSql, fieldsVal.toArray());
    }

    /**
     * 批量插入记录
     */
    @Override
    @CheckExecute(target = ExecuteMethod.INSERT)
    public <T> int insert(List<T> tList, boolean isGeneratedKey) throws Exception {
        T t = tList.get(0);
        //数据库字段
        String[] dbFields = dbParserFieldHandler.getDbFields(t.getClass());
        StringJoiner dbFieldStr = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2, SymbolConst.BRACKETS_LEFT, SymbolConst.BRACKETS_RIGHT);

        //如果存在主键
        boolean existKey = dbParserFieldHandler.isDbKeyTag(t.getClass());
        String dbFieldKey = SymbolConst.EMPTY;
        if (existKey) {
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
            if (existKey) {
                fieldsVal.add(0, dbParserFieldHandler.generateKey(obj));
            }
            saveValues.addAll(fieldsVal);
            /* 拼接预编译的? */
            StringJoiner insertSymbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2, SymbolConst.BRACKETS_LEFT, SymbolConst.BRACKETS_RIGHT);
            int symbol = fieldsVal.size();
            do {
                insertSymbol.add(SymbolConst.QUEST);
                symbol--;
            } while (symbol > 0);
            inertValStr.add("\n" + insertSymbol.toString());
        }
        String insertSql = String.format("insert into `%s` %s values %s",
                dbParserFieldHandler.getDbTableName(t.getClass()), dbFieldStr, inertValStr);
        Field fieldKeyType = dbParserFieldHandler.getFieldKeyType(t.getClass());
        return isGeneratedKey ?
                sqlExecuteAction.executeInsert(tList, insertSql, dbFieldKey, fieldKeyType.getType(), saveValues.toArray()) :
                sqlExecuteAction.executeUpdate(insertSql, saveValues.toArray());
    }

    /* ----------------------------------------------------------------update---------------------------------------------------------------- */

    /**
     * 根据指定字段修改一条记录
     */
    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByKey(T t, String... updateDbFields) throws Exception {
        StringJoiner editSymbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_1);
        List<String> updateDbColumns = new ArrayList<>();
        List<Object> updateDbValues = new ArrayList<>();

        if (updateDbFields == null || updateDbFields.length == 0) {
            List<String> dbFields = Arrays.stream(dbParserFieldHandler.getDbFields(t.getClass())).collect(Collectors.toList());
            List<Object> fieldsVal = dbParserFieldHandler.getFieldsVal(t, dbParserFieldHandler.getFiledNames(t.getClass()));
            for (int i = 0; i < dbFields.size(); i++) {
                if (JudgeUtilsAx.isEmpty(fieldsVal.get(i))) {
                    continue;
                }
                updateDbColumns.add(dbFields.get(i));
                updateDbValues.add(fieldsVal.get(i));
            }
        } else {
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
        return sqlExecuteAction.executeUpdate(updateSql, updateDbValues.toArray());
    }

    @Override
    public <T> int updateByCondition(T t, ConditionEntity<T> conditionEntity) throws Exception {
        return 0;
    }

    /* ----------------------------------------------------------------common---------------------------------------------------------------- */

    /**
     * 保存（更新或插入）
     */
    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> long save(T t) throws Exception {
        if (CustomUtil.isKeyTag(t.getClass())) {
            throw new CustomCheckException(ExceptionConst.EX_DBKEY_NOTFOUND + t);
        }
        long update = updateByKey(t);
        if (update == 0) {
            update = insert(t, false);
        }
        return update;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.NONE)
    public void createTables(Class<?>... arr) throws Exception {
        buildTableHandler.createTables(arr);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.NONE)
    public void dropTables(Class<?>... arr) throws Exception {
        buildTableHandler.dropTables(arr);
    }


}
