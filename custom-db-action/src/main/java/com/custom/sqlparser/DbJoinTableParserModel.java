package com.custom.sqlparser;

import com.custom.annotations.DbMapper;
import com.custom.dbaction.AbstractTableModel;

import java.lang.reflect.Field;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/7 14:40
 * @Desc：
 **/
public class DbJoinTableParserModel<T> extends AbstractTableModel<T> {

    private Field field;

    private String joinName;

    private String fieldName;

    private boolean underlineToCamel;

    public DbJoinTableParserModel(Field field, boolean underlineToCamel) {
        DbMapper dbMap = field.getAnnotation(DbMapper.class);
        this.joinName = dbMap.value();
        this.fieldName = field.getName();
        this.field = field;
        this.underlineToCamel = underlineToCamel;
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
    protected String buildTableSql() {
        return null;
    }

    @Override
    protected Object getValue(T x) {
        return null;
    }

    @Override
    public String getFieldSql() {
        return joinName;
    }

    @Override
    protected String getSelectFieldSql() {
        return String.format("%s %s", joinName, fieldName);
    }

    @Override
    protected String getSelectFieldSql(String column) {
        return String.format("%s %s", column, fieldName);
    }
}
