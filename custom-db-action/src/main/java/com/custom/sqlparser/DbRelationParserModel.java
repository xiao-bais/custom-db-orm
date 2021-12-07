package com.custom.sqlparser;

import com.custom.annotations.*;
import com.custom.comm.CustomUtil;
import com.custom.dbconfig.SymbolConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/2 16:34
 * @Desc：对于@DbRelated注解的解析
 **/
public class DbRelationParserModel<T> extends AbstractTableModel<T> {

    private Class<T> cls;

    private String joinTable;

    private String joinAlias;

    private String condition;

    private String joinStyle;

    private String fieldName;

    private String column;


    public DbRelationParserModel(Class<T> cls, Field field, String table, String alias) {
        this.cls = cls;
        this.setTable(table);
        this.setAlias(alias);
        DbRelated annotation = field.getAnnotation(DbRelated.class);
        this.joinTable = annotation.joinTable();
        this.column = annotation.field();
        this.fieldName = field.getName();
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
        return null;
    }


    @Override
    public Object getValue(T x, String fieldName) {
        return null;
    }

    @Override
    public String getFieldSql() {
        return String.format("%s.`%s`", this.joinAlias, this.column);
    }

    @Override
    public String getSelectFieldSql() {
        return String.format("%s.`%s` `%s`", this.joinAlias, this.column, this.fieldName);
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

    public String getJoinStyle() {
        return joinStyle;
    }

    public void setJoinStyle(String joinStyle) {
        this.joinStyle = joinStyle;
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
}
