package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractTableModel;
import com.custom.action.util.DbUtil;
import com.custom.comm.GlobalDataHandler;
import com.custom.comm.JudgeUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.annotations.DbMapper;
import com.custom.comm.exceptions.ExThrowsUtil;

import java.lang.reflect.Field;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/7 14:40
 * @Desc：
 **/
public class DbJoinTableParserModel<T> extends AbstractTableModel<T> {

    /**
     * 关联字段属性
     */
    private Field field;

    /**
     * 关联表的查询字段
     */
    private String joinName;

    /**
     * 关联字段属性名称
     */
    private String fieldName;

    /**
     * 查询时，指定查询字段的包装
     * 例：concat('user-', a.name) columnName
     */
    private String wrapperColumn;

    /**
     * 查询时若当前字段为字符串类型，是否null转为空字符串
     */
    private Boolean isNullToEmpty = false;

    /**
     * 当初查询实体的Class对象
     */
    private final Class<T> entityClass;



    public DbJoinTableParserModel(Class<T> entityClass, Field field) {
        this.entityClass = entityClass;
        this.fieldName = field.getName();
        if(GlobalDataHandler.hasSqlKeyword(fieldName)) {
            this.fieldName = GlobalDataHandler.wrapperSqlKeyword(fieldName);
        }
        initJoinName(field);
        this.field = field;
    }

    private void initJoinName(Field field) {
        DbMapper dbMap = field.getAnnotation(DbMapper.class);
        this.joinName = JudgeUtil.isEmpty(dbMap.value()) ? field.getName() : dbMap.value();
        this.wrapperColumn = dbMap.wrapperColumn();
        this.isNullToEmpty = dbMap.isNullToEmpty();
        if(!joinName.contains(SymbolConstant.POINT)) {
            ExThrowsUtil.toCustom("%s.[%s] 在DBMapper注解上未指定关联表的别名", this.entityClass, this.fieldName);
        }
        int pointIndex = joinName.indexOf(SymbolConstant.POINT);
        String fieldPrefix = joinName.substring(0, pointIndex);
        String fieldSuffix = joinName.substring(pointIndex + 1);
        if(GlobalDataHandler.hasSqlKeyword(fieldSuffix)) {
            fieldSuffix = GlobalDataHandler.wrapperSqlKeyword(fieldSuffix);
        }
        this.joinName = fieldPrefix + SymbolConstant.POINT + fieldSuffix;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getJoinName() {
        return joinName;
    }

    public void setJoinName(String joinName) {
        this.joinName = joinName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String buildTableSql() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object getValue(T x) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void setValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFieldSql() {
        return joinName;
    }

    @Override
    protected String getSelectFieldSql() {
        if (JudgeUtil.isNotEmpty(this.wrapperColumn)) {
            return DbUtil.wrapperSqlColumn(this.wrapperColumn, this.fieldName, this.isNullToEmpty);
        }
        return String.format("%s %s", joinName, fieldName);
    }
}
