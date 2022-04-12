package com.custom.dbaction;

import com.custom.dbconfig.SymbolConst;
import com.custom.sqlparser.DbFieldParserModel;
import com.custom.sqlparser.DbKeyParserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Xiao-Bai
 * @date 2022/4/3 17:33
 * @desc:
 */
public abstract class AbstractSqlBuilder<T> {

    private String table;
    private String alias;
    private T entity;
    private Class<T> entityClass;
    private DbKeyParserModel<T> keyParserModel;
    private List<DbFieldParserModel<T>> fieldParserModels;
    private Map<String, String> fieldMapper;
    private Map<String, String> columnMapper;
    private String logicColumn;
    private Object logicDeleteValue;
    private Object logicNotDeleteValue;
    private String logicDeleteQuerySql;
    private String logicDeleteUpdateSql;
    private List<Object> sqlParams = new ArrayList<>();

    // 构建sql语句
    protected abstract String buildSql();

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

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public List<Object> getSqlParams() {
        return sqlParams;
    }

    public void setSqlParams(List<Object> sqlParams) {
        this.sqlParams = sqlParams;
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
        this.logicDeleteValue = logicDeleteValue;
    }

    public Object getLogicNotDeleteValue() {
        return logicNotDeleteValue;
    }

    public void setLogicNotDeleteValue(Object logicNotDeleteValue) {
        this.logicNotDeleteValue = logicNotDeleteValue;
    }

    public String getLogicDeleteQuerySql() {
        if(Objects.isNull(this.logicDeleteQuerySql)) {
            this.logicDeleteQuerySql = logicColumn + SymbolConst.EQUALS + logicNotDeleteValue;
        }
        return this.logicDeleteQuerySql;
    }

    public String getLogicDeleteUpdateSql() {
        if(Objects.isNull(this.logicDeleteQuerySql)) {
            this.logicDeleteQuerySql = logicColumn + SymbolConst.EQUALS + logicNotDeleteValue;
        }
        return this.logicDeleteQuerySql = logicColumn + SymbolConst.EQUALS + logicDeleteValue;
    }
}
