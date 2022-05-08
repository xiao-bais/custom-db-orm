package com.custom.action.dbaction;

import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.wrapper.ColumnParseHandler;
import com.custom.action.sqlparser.DbFieldParserModel;
import com.custom.action.sqlparser.DbKeyParserModel;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.jdbc.SqlExecuteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Xiao-Bai
 * @date 2022/4/3 17:33
 * @desc: sql操作模板父类
 */
public abstract class AbstractSqlBuilder<T> {

    private String table;
    private String alias;
    private T entity;
    private List<T> entityList;
    private Class<T> entityClass;
    private DbKeyParserModel<T> keyParserModel;
    private List<DbFieldParserModel<T>> fieldParserModels;
    private Map<String, String> fieldMapper;
    private Map<String, String> columnMapper;
    private SqlExecuteAction sqlExecuteAction;
    private ColumnParseHandler<T> columnParseHandler;
    private Boolean primaryTable = false;
    private String logicColumn;
    private Object logicDeleteValue;
    private Object logicNotDeleteValue;
    private String logicDeleteQuerySql;
    private String logicDeleteUpdateSql;
    private List<Object> sqlParams = new ArrayList<>();

    // 构建sql语句
    public abstract String buildSql();

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
        if (Objects.nonNull(keyParserModel)) {
            keyParserModel.setEntity(entity);
        }
        if (!fieldParserModels.isEmpty()) {
            fieldParserModels.forEach(x -> x.setEntity(entity));
        }
    }

    public List<T> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<T> entityList) {
        this.entityList = entityList;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.columnParseHandler = new ColumnParseHandler<>(entityClass);
    }

    public List<Object> getSqlParams() {
        if (Objects.isNull(sqlParams)) {
            sqlParams = new ArrayList<>();
        }
        return sqlParams;
    }

    public void setSqlParams(List<Object> sqlParams) {
        if (Objects.nonNull(sqlParams)) {
            this.sqlParams = sqlParams;
        }
    }

    public DbKeyParserModel<T> getKeyParserModel() {
        return keyParserModel;
    }

    public void setKeyParserModel(DbKeyParserModel<T> keyParserModel) {
        this.keyParserModel = keyParserModel;
    }

    public List<DbFieldParserModel<T>> getFieldParserModels() {
        return fieldParserModels;
    }

    public void setFieldParserModels(List<DbFieldParserModel<T>> fieldParserModels) {
        this.fieldParserModels = fieldParserModels;
    }

    public Map<String, String> getFieldMapper() {
        return fieldMapper;
    }

    public void setFieldMapper(Map<String, String> fieldMapper) {
        this.fieldMapper = fieldMapper;
    }

    public Map<String, String> getColumnMapper() {
        return columnMapper;
    }

    public void setColumnMapper(Map<String, String> columnMapper) {
        this.columnMapper = columnMapper;
    }

    public String getLogicColumn() {
        return logicColumn;
    }

    public void setLogicColumn(String logicColumn) {
        this.logicColumn = logicColumn;
    }

    public Object getLogicDeleteValue() {
        return logicDeleteValue;
    }

    public void setLogicDeleteValue(Object logicDeleteValue) {
        if (logicDeleteValue instanceof CharSequence) {
            logicDeleteValue = String.format("'%s'", logicDeleteValue);
        }
        this.logicDeleteValue = logicDeleteValue;
        this.logicDeleteUpdateSql = String.format("%s.%s = %s", alias, logicColumn, logicDeleteValue);
    }

    public Object getLogicNotDeleteValue() {
        return logicNotDeleteValue;
    }

    public void setLogicNotDeleteValue(Object logicNotDeleteValue) {
        if (logicNotDeleteValue instanceof CharSequence) {
            logicNotDeleteValue = String.format("'%s'", logicNotDeleteValue);
        }
        this.logicNotDeleteValue = logicNotDeleteValue;
        this.logicDeleteQuerySql = String.format("%s.%s = %s", alias, logicColumn, logicNotDeleteValue);
    }

    public String getLogicDeleteQuerySql() {
        return this.logicDeleteQuerySql;
    }

    public String getLogicDeleteUpdateSql() {
        return this.logicDeleteUpdateSql;
    }

    public void setSqlExecuteAction(SqlExecuteAction sqlExecuteAction) {
        this.sqlExecuteAction = sqlExecuteAction;
    }

    public ColumnParseHandler<T> getColumnParseHandler() {
        return columnParseHandler;
    }

    public Boolean getPrimaryTable() {
        return primaryTable;
    }

    public void setPrimaryTable(Boolean primaryTable) {
        this.primaryTable = primaryTable;
    }

    /**
     * 直接执行，属于内部执行
     */
    public void executeUpdateNotPrintSql(String sql) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            throw new NullPointerException();
        }
        sqlExecuteAction.executeUpdateNotPrintSql(sql);
    }

    /**
     * 由于部分表可能没有逻辑删除字段，所以在每一次执行时，都需检查该表有没有逻辑删除的字段，以保证sql正常执行
     */
    public boolean checkLogicFieldIsExist() throws Exception {
        if (CustomUtil.isBlank(logicColumn)) {
            return false;
        }
        Boolean existsLogic = TableInfoCache.isExistsLogic(table);
        if (existsLogic != null) {
            return existsLogic;
        }
        String existSql = String.format("select count(*) count from information_schema.columns where table_name = '%s' and column_name = '%s'", table, logicColumn);
        long count = sqlExecuteAction.executeExist(existSql);
        TableInfoCache.setTableLogic(table, count > 0);
        return count > 0;
    }
}
