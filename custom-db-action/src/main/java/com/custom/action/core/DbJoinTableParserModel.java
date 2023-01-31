package com.custom.action.core;

import com.custom.action.dbaction.AbstractTableModel;
import com.custom.action.util.DbUtil;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.jdbc.configuration.GlobalDataHandler;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.utils.Constants;
import com.custom.comm.annotations.DbJoinField;

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
        DbJoinField joinField = field.getAnnotation(DbJoinField.class);
        this.joinName = JudgeUtil.isEmpty(joinField.value()) ? this.fieldName : joinField.value();
        if(!joinName.contains(Constants.POINT)) {
            throw new CustomCheckException("%s.[%s] 在DBMapper注解上未指定关联表的别名", this.entityClass, this.fieldName);
        }
        int pointIndex = joinName.indexOf(Constants.POINT);
        String fieldPrefix = joinName.substring(0, pointIndex);
        String fieldSuffix = joinName.substring(pointIndex + 1);
        if(GlobalDataHandler.hasSqlKeyword(fieldSuffix)) {
            fieldSuffix = GlobalDataHandler.wrapperSqlKeyword(fieldSuffix);
        }
        this.joinName = fieldPrefix + Constants.POINT + fieldSuffix;
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
    public String createTableSql() {
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
        return DbUtil.sqlSelectWrapper(joinName, fieldName);
    }
}
