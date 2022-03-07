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

    public DbJoinTableParserModel(Field field) {
        DbMapper dbMap = field.getAnnotation(DbMapper.class);
        this.joinName = dbMap.value();
        this.fieldName = field.getName();
        this.field = field;
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
        return null;
    }

    @Override
    public Object getValue(T x) {
        return null;
    }

    @Override
    public String getFieldSql() {
        return joinName;
    }

    @Override
    public String getSelectFieldSql() {
        return String.format("%s %s", joinName, fieldName);
    }

    @Override
    public String getSelectFieldSql(String column) {
        return String.format("%s %s", column, fieldName);
    }
}
