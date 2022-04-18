package com.custom.action.sqlparser;

import com.custom.action.annotations.DbKey;
import com.custom.action.comm.GlobalDataHandler;
import com.custom.action.dbaction.AbstractTableModel;
import com.custom.action.dbconfig.SymbolConst;
import com.custom.action.enums.DbMediaType;
import com.custom.action.enums.KeyStrategy;
import com.custom.action.exceptions.CustomCheckException;
import com.custom.action.comm.CustomUtil;
import com.custom.action.comm.JudgeUtilsAx;
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

    private DbMediaType dbMediaType;

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

    public DbMediaType getDbMediaType() {
        return dbMediaType;
    }

    public void setDbMediaType(DbMediaType dbMediaType) {
        this.dbMediaType = dbMediaType;
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
                    throw new CustomCheckException("The value of the primary key is empty");
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

    @Override
    protected String getSelectFieldSql(String column) {
        return String.format("%s %s", column, this.key);
    }

    protected void setValue(Object value) {
        super.setFieldValue(this.entity, this.field, value);
    }


    @Override
    protected String buildTableSql() {
        StringBuilder keyFieldSql = new StringBuilder(String.format("`%s` ", this.dbKey));
        keyFieldSql.append(this.dbMediaType.getType())
                .append(SymbolConst.BRACKETS_LEFT)
                .append(this.length)
                .append(SymbolConst.BRACKETS_RIGHT)
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
        this.dbMediaType = annotation.dbType();
        this.strategy = annotation.strategy();
        this.desc = annotation.desc();
        this.length = this.dbMediaType.getLength();
        this.setTable(table);
        this.setAlias(alias);
    }


}
