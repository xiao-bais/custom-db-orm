package com.custom.sqlparser;

import com.custom.annotations.*;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbaction.AbstractTableModel;
import com.custom.enums.DbJoinStyle;

import java.lang.reflect.Field;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/2 16:34
 * @Desc：对于@DbRelated注解的解析
 **/
public class DbRelationParserModel<T> extends AbstractTableModel<T> {

    private Class<T> cls;

    private String joinTable;

    private String joinAlias;

    private boolean underlineToCamel;

    private Field field;

    private String condition;

    private DbJoinStyle joinStyle;

    private String fieldName;

    private String column;


    public DbRelationParserModel(Class<T> cls, Field field, String table, String alias, boolean underlineToCamel) {
        this.cls = cls;
        this.setTable(table);
        this.setAlias(alias);
        DbRelated annotation = field.getAnnotation(DbRelated.class);
        this.joinTable = annotation.joinTable();
        this.fieldName = field.getName();
        if (JudgeUtilsAx.isEmpty(annotation.field())) {
            this.column = underlineToCamel ? CustomUtil.camelToUnderline(this.fieldName) : this.fieldName;
        }else {
            this.column = annotation.field();
        }
        this.underlineToCamel = underlineToCamel;
        this.field = field;
        this.joinAlias = annotation.joinAlias();
        this.joinStyle = annotation.joinStyle();
        this.condition = annotation.condition();
    }

    public DbRelationParserModel(Class<T> cls) {
        this.cls = cls;
        DbTable annotation = cls.getAnnotation(DbTable.class);
        this.setTable(annotation.table());
        this.setAlias(annotation.alias());
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
        return String.format("%s.%s", this.joinAlias, this.column);
    }

    @Override
    protected String getSelectFieldSql() {
        return String.format("%s.%s %s", this.joinAlias, this.column, this.fieldName);
    }

    @Override
    protected String getSelectFieldSql(String column) {
        return String.format("%s %s", column, this.fieldName);
    }


    public String getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(String joinTable) {
        this.joinTable = joinTable;
    }

    public String getJoinAlias() {
        return joinAlias;
    }

    public void setJoinAlias(String joinAlias) {
        this.joinAlias = joinAlias;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public DbJoinStyle getJoinStyle() {
        return joinStyle;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Field getField() {
        return field;
    }
}
