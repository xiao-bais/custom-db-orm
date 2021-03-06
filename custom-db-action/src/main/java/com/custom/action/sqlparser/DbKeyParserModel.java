package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractTableModel;
import com.custom.comm.CustomUtil;
import com.custom.comm.GlobalDataHandler;
import com.custom.comm.JudgeUtilsAx;
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

    private Field field;

    private DbType dbType;

    private KeyStrategy strategy;

    private Class<?> type;

    private String length;

    private String desc;

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

    public DbType getDbMediaType() {
        return dbType;
    }

    public void setDbMediaType(DbType dbType) {
        this.dbType = dbType;
    }

    public KeyStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(KeyStrategy strategy) {
        this.strategy = strategy;
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
        }catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
            return null;
        }catch (NullPointerException npe) {
            return null;
        }
        return value;
    }

    @Override
    protected Object getValue(T t) {
        try {
            this.value = getFieldValue(t, key);
        }catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
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
        return String.format("%s.%s", this.getAlias(), this.dbKey);
    }

    @Override
    public String getSelectFieldSql() {
        return String.format("%s.%s %s", this.getAlias(), this.dbKey, this.key);
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
                .append(" PRIMARY KEY ");

        if(this.strategy == KeyStrategy.AUTO) {
            keyFieldSql.append(" AUTO_INCREMENT ");
        }
        keyFieldSql.append(" NOT NULL ").append(String.format(" COMMENT '%s'", this.desc));
        return keyFieldSql.toString();
    }

    public DbKeyParserModel(){}


    public DbKeyParserModel(T t, Field field, String table, String alias, boolean underlineToCamel){
        this(field, table, alias, underlineToCamel);
        this.entity = t;
    }

    public DbKeyParserModel(Field field, String table, String alias, boolean underlineToCamel){
        this.key = GlobalDataHandler.hasSqlKeyword(field.getName()) ? String.format("`%s`", field.getName()) : field.getName();
        DbKey annotation = field.getAnnotation(DbKey.class);
        if (JudgeUtilsAx.isEmpty(annotation.value())) {
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
