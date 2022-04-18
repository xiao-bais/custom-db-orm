package com.custom.action.sqlparser;

import com.custom.action.annotations.DbField;
import com.custom.action.comm.GlobalDataHandler;
import com.custom.action.comm.JudgeUtilsAx;
import com.custom.action.dbaction.AbstractTableModel;
import com.custom.action.dbconfig.SymbolConst;
import com.custom.action.enums.DbMediaType;
import com.custom.action.comm.CustomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Xiao-Bai
 * @date 2021/12/1 23:39
 * @desc:对于@DbField注解的解析
 */
public class DbFieldParserModel<T> extends AbstractTableModel<T> {

    private static final Logger logger = LoggerFactory.getLogger(DbFieldParserModel.class);

    private T entity;

    /**
    * java字段名称
    */
    private String fieldName;

    /**
    * sql列名
    */
    private String column;

    /**
    * sql字段类型
    */
    private DbMediaType dbMediaType;

    /**
    * java字段类型
    */
    private Class<?> type;

    /**
     * 字段属性
     */
    private Field field;

    /**
    * 字段值
    */
    private Object value;

    /**
    * sql字段长度
    */
    private String length;

    /**
    * sql字段说明
    */
    private String desc;

    /**
    * 是否为空
    */
    private boolean isNull;


    /**
    * 构建创建表的sql语句
    */
    @Override
    protected String buildTableSql(){
        StringBuilder fieldSql = new StringBuilder(String.format("`%s` ", this.column));
        if(dbMediaType == DbMediaType.DbDate || dbMediaType == DbMediaType.DbDateTime)
            fieldSql.append(dbMediaType.getType()).append(" ");
        else
            fieldSql.append(dbMediaType.getType())
                    .append(SymbolConst.BRACKETS_LEFT)
                    .append(this.length)
                    .append(SymbolConst.BRACKETS_RIGHT).append(" ");

        fieldSql.append(this.isNull ? "NULL" : "NOT NULL").append(" ");
        fieldSql.append(String.format(" COMMENT '%s'", this.desc));
        return fieldSql.toString();
    }

    public DbFieldParserModel(){}

    public DbFieldParserModel(T t, Field field, String table, String alias, boolean underlineToCamel) {
        this(field, table, alias, underlineToCamel);
        this.entity = t;
    }

    public DbFieldParserModel(Field field, String table, String alias, boolean underlineToCamel) {
        this.fieldName = GlobalDataHandler.hasSqlKeyword(field.getName()) ? String.format("`%s`", field.getName()) : field.getName();
        this.type = field.getType();
        DbField annotation = field.getAnnotation(DbField.class);
        this.field = field;
        if (JudgeUtilsAx.isEmpty(annotation.value())) {
            this.column = underlineToCamel ? CustomUtil.camelToUnderline(this.fieldName) : this.fieldName;
        }else {
            this.column = annotation.value();
        }
        if(GlobalDataHandler.hasSqlKeyword(column)) {
            this.column = String.format("`%s`", column);
        }
        this.isNull = annotation.isNull();
        this.desc = annotation.desc();
        this.dbMediaType = annotation.dataType() == DbMediaType.DbVarchar ? CustomUtil.getDbFieldType(field.getType()) : annotation.dataType();
        this.length = this.dbMediaType.getLength();
        super.setTable(table);
        super.setAlias(alias);
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

    public DbMediaType getDbMediaType() {
        if(this.dbMediaType == DbMediaType.DbVarchar) {
            this.dbMediaType = CustomUtil.getDbFieldType(this.type);
        }
        return dbMediaType;
    }

    public void setDbMediaType(DbMediaType dbMediaType) {
        this.dbMediaType = dbMediaType;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    @Override
    protected Object getValue(T x) {
        try {
            value = getFieldValue(x, fieldName);
        }catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return value;
    }

    @Override
    public String getFieldSql() {
        return String.format("%s.%s", this.getAlias(), this.column);
    }

    @Override
    public String getSelectFieldSql() {
        return String.format("%s.%s %s", this.getAlias(), this.column, this.fieldName);
    }

    @Override
    protected String getSelectFieldSql(String column) {
        return String.format("%s %s", column, this.fieldName);
    }

    public Object getValue() {
        try {
            value = getFieldValue(entity, fieldName);
        }catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return value;
    }

    protected void setValue(Object value) {
        super.setFieldValue(entity, this.field, value);
    }

    public Field getField() {
        return field;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
