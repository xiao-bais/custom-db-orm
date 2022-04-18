package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractTableModel;
import com.custom.action.annotations.DbMapper;
import com.custom.action.comm.GlobalDataHandler;
import com.custom.action.comm.JudgeUtilsAx;
import com.custom.action.dbconfig.SymbolConst;

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
        initJoinName(field);
        this.fieldName = field.getName();
        if(GlobalDataHandler.hasSqlKeyword(fieldName)) {
            this.fieldName = String.format("`%s`", this.fieldName);
        }
        this.field = field;
    }

    private void initJoinName(Field field) {
        DbMapper dbMap = field.getAnnotation(DbMapper.class);
        this.joinName = JudgeUtilsAx.isEmpty(dbMap.value()) ? field.getName() : dbMap.value();
        if(!joinName.contains(SymbolConst.POINT)) {
            return;
        }
        int pointIndex = joinName.indexOf(SymbolConst.POINT);
        String fieldPrefix = joinName.substring(0, pointIndex);
        String fieldSuffix = joinName.substring(pointIndex + 1);
        if(GlobalDataHandler.hasSqlKeyword(fieldSuffix)) {
            fieldSuffix = String.format("`%s`", fieldSuffix);
        }
        this.joinName = fieldPrefix + SymbolConst.POINT + fieldSuffix;
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
        return String.format("%s %s", joinName, fieldName);
    }

    @Override
    protected String getSelectFieldSql(String column) {
        return String.format("%s %s", column, fieldName);
    }
}
