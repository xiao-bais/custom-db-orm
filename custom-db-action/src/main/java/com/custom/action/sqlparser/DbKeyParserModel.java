package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractTableModel;
import com.custom.action.util.DbUtil;
import com.custom.comm.CustomUtil;
import com.custom.jdbc.GlobalDataHandler;
import com.custom.comm.JudgeUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.enums.DbType;
import com.custom.comm.enums.KeyStrategy;
import com.custom.comm.exceptions.ExThrowsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Xiao-Bai
 * @date 2021/12/2 0:44
 * @desc: 对于@DbKey的字段解析
 */
public class DbKeyParserModel<T> extends AbstractTableModel<T> {

    private static final Logger logger = LoggerFactory.getLogger(DbKeyParserModel.class);

    private T entity;

    private String dbKey;

    private String key;

    private final Field field;

    private final DbType dbType;

    private final KeyStrategy strategy;

    private final Class<?> type;

    private final String length;

    private final String desc;

    private Object value;


    public String getDbKey() {
        return dbKey;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public Class<?> getType() {
        return type;
    }

    public Field getField() {
        return field;
    }

    protected Object getValue() {
        if(entity == null) throw new NullPointerException();
        try {
            this.value = getFieldValue(entity, key);
        }catch (InvocationTargetException | IllegalAccessException
                | NoSuchMethodException | NoSuchFieldException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return value;
    }

    @Override
    protected Object getValue(T t) {
        try {
            this.value = getFieldValue(t, key);
        }catch (InvocationTargetException | IllegalAccessException
                | NoSuchMethodException | NoSuchFieldException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return value;
    }

    /**
    * 生成主键
    */
    protected Object generateKey() {
        Object key = null;
        switch (strategy) {
            case AUTO:
                if(type.equals(Integer.class) || type.equals(int.class)) {
                    key = 0;
                }else if(type.equals(Long.class) || type.equals(long.class)) {
                    key = 0L;
                }
                break;
            case UUID:
                key = CustomUtil.getUUID();
                this.setValue(key);
                break;
            case INPUT:
                key = getValue();
                if(key == null) {
                    ExThrowsUtil.toCustom("The value of the primary key is empty");
                }
                break;
        }
        return key;
    }

    @Override
    public String getFieldSql() {
        return DbUtil.fullSqlColumn(this.getAlias(), this.dbKey);
    }

    @Override
    public String getSelectFieldSql() {
        return DbUtil.sqlSelectWrapper(DbUtil.fullSqlColumn(this.getAlias(), this.dbKey), this.key);
    }

    protected void setValue(Object value) {
        super.setFieldValue(this.entity, this.field, value);
    }


    @Override
    public String buildTableSql() {
        StringBuilder keyFieldSql = new StringBuilder(String.format("`%s` ", this.dbKey));
        keyFieldSql.append(this.dbType.getType())
                .append(SymbolConstant.BRACKETS_LEFT)
                .append(this.length)
                .append(SymbolConstant.BRACKETS_RIGHT)
                .append(" primary key ");

        if(this.strategy == KeyStrategy.AUTO) {
            keyFieldSql.append(" auto_increment ");
        }
        keyFieldSql.append(" not null ").append(String.format(" comment '%s'", this.desc));
        return keyFieldSql.toString();
    }


    public DbKeyParserModel(T t, Field field, String table, String alias, boolean underlineToCamel){
        this(field, table, alias, underlineToCamel);
        this.entity = t;
    }

    public DbKeyParserModel(Field field, String table, String alias, boolean underlineToCamel){
        this.key = GlobalDataHandler.hasSqlKeyword(field.getName()) ? String.format("`%s`", field.getName()) : field.getName();
        DbKey annotation = field.getAnnotation(DbKey.class);
        if (JudgeUtil.isEmpty(annotation.value())) {
            this.dbKey = underlineToCamel ? CustomUtil.camelToUnderline(this.key) : this.key;
        }else {
            this.dbKey = annotation.value();
        }
        this.dbKey = GlobalDataHandler.hasSqlKeyword(dbKey) ? String.format("`%s`", dbKey) : dbKey;
        this.field = field;
        this.type = field.getType();
        this.dbType = annotation.dbType();
        this.strategy = annotation.strategy();
        this.desc = annotation.desc();
        this.length = this.dbType.getLength();
        this.setTable(table);
        this.setAlias(alias);
    }


}
