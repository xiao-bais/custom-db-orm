package com.custom.action.core;

import com.custom.action.dbaction.AbstractTableModel;
import com.custom.action.util.DbUtil;
import com.custom.comm.annotations.DbField;
import com.custom.comm.enums.DbType;
import com.custom.comm.enums.FillStrategy;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.jdbc.configuration.GlobalDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * 对于@DbField注解的解析
 * @author   Xiao-Bai
 * @since  2021/12/1 23:39
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
    private DbType dbType;

    /**
    * java字段类型
    */
    private final Class<?> type;

    /**
     * 字段属性
     */
    private final Field field;

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
     * 是否标注了DbField注解
     */
    private final boolean existsDbField;

    /**
     * 是否是表字段
     */
    private final boolean isDbField;

    /**
     * 填充策略
     */
    private FillStrategy fillStrategy = FillStrategy.DEFAULT;


    public DbFieldParserModel(Field field, String table, String alias, boolean underlineToCamel, boolean existsDbField) {
        if (GlobalDataHandler.hasSqlKeyword(field.getName())) {
            this.fieldName =  GlobalDataHandler.wrapperSqlKeyword(field.getName());
        }else {
            this.fieldName = field.getName();
        }
        this.type = field.getType();
        this.field = field;
        this.existsDbField = existsDbField;
        this.isDbField = true;

        if (existsDbField) {
            DbField annotation = field.getAnnotation(DbField.class);
            if (JudgeUtil.isEmpty(annotation.value())) {
                this.column = underlineToCamel ? CustomUtil.camelToUnderline(this.fieldName) : this.fieldName;
            }else {
                this.column = annotation.value();
            }
            this.isNull = annotation.isNull();
            this.desc = annotation.desc();
            this.dbType = annotation.dataType() == DbType.DbVarchar ? DbType.getDbMediaType(field.getType()) : annotation.dataType();
            this.length = this.dbType.getLength();
            this.fillStrategy = annotation.strategy();
        }else {
            this.column = underlineToCamel ? CustomUtil.camelToUnderline(this.fieldName) : this.fieldName;
        }

        if(GlobalDataHandler.hasSqlKeyword(column)) {
            this.column = GlobalDataHandler.wrapperSqlKeyword(column);
        }
        super.setTable(table);
        super.setAlias(alias);
    }

    public DbFieldParserModel(Field field) {
        this.fieldName = field.getName();
        this.type = field.getType();
        this.field = field;
        this.existsDbField = false;
        this.isDbField = false;
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

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public boolean isDbField() {
        return isDbField;
    }

    public FillStrategy getFillStrategy() {
        return fillStrategy;
    }

    /**
     * 构建创建表的sql语句
     */
    @Override
    public String createTableSql() {
        if (!isDbField) {
            return Constants.EMPTY;
        }
        StringBuilder createSql = new StringBuilder(this.column).append(" ");
        if (dbType == DbType.DbDate || dbType == DbType.DbDateTime)
            createSql.append(dbType.getType()).append(" ");
        else
            createSql.append(dbType.getType())
                    .append(Constants.BRACKETS_LEFT)
                    .append(this.length)
                    .append(Constants.BRACKETS_RIGHT).append(" ");

        return createSql.append(this.isNull ? "NULL" : "NOT NULL").append(" ")
                .append(String.format(" COMMENT '%s'", this.desc)).toString();
    }

    @Override
    protected Object getValue(T currEntity) {
        Object val;
        try {
            String readValueField = fieldName;
            if (GlobalDataHandler.isSqlKeywordWrapping(fieldName)) {
                readValueField = GlobalDataHandler.removeSqlKeywordWrapper(fieldName);
            }
            val = getFieldValue(currEntity, readValueField);
        }catch (InvocationTargetException | IllegalAccessException
                | NoSuchMethodException | NoSuchFieldException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return val;
    }

    @Override
    public String getFieldSql() {
        if (!isDbField) {
            return Constants.EMPTY;
        }
        return DbUtil.fullSqlColumn(this.getAlias(), this.column);
    }

    @Override
    public String getSelectFieldSql() {
        if (!isDbField) {
            return Constants.EMPTY;
        }
        String column = DbUtil.fullSqlColumn(this.getAlias(), this.column);
        return DbUtil.sqlSelectWrapper(column, this.fieldName);
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

    public boolean isExistsDbField() {
        return existsDbField;
    }

}
