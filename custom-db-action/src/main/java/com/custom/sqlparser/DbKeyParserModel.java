package com.custom.sqlparser;

import com.custom.annotations.DbKey;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbaction.AbstractTableModel;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.DbMediaType;
import com.custom.enums.KeyStrategy;
import com.custom.exceptions.CustomCheckException;
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

    private static Logger logger = LoggerFactory.getLogger(DbKeyParserModel.class);

    private T t;

    private String dbKey;

    private String key;

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

    public Object getValue() {
        if(t == null) throw new NullPointerException();
        try {
            this.value = getFieldValue(t, key);
        }catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
            return null;
        }catch (NullPointerException npe) {
            return null;
        }
        return value;
    }

    @Override
    public Object getValue(T t) {
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
    public Object generateKey() {
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

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String buildTableSql() {

        StringBuilder keyFieldSql = new StringBuilder(String.format("`%s` ", this.dbKey));
        keyFieldSql.append(this.dbMediaType.getType())
                .append(SymbolConst.BRACKETS_LEFT)
                .append(this.length)
                .append(SymbolConst.BRACKETS_RIGHT).append(" PRIMARY KEY ");

        if(this.strategy == KeyStrategy.AUTO)
            keyFieldSql.append(" AUTO_INCREMENT ");

        keyFieldSql.append(" NOT NULL ")
                .append(String.format(" COMMENT '%s'", this.desc));
        return keyFieldSql.toString();
    }

    public DbKeyParserModel(){
    }


    public DbKeyParserModel(T t, Field field, String table, String alias){
        this(field, table, alias);
        this.t = t;
    }

    public DbKeyParserModel(Field field, String table, String alias){
        this.key = field.getName();
        DbKey annotation = field.getAnnotation(DbKey.class);
        this.dbKey = JudgeUtilsAx.isEmpty(annotation.value()) ? this.key : annotation.value();
        this.type = field.getType();
        this.dbMediaType = annotation.dbType();
        this.strategy = annotation.strategy();
        this.desc = annotation.desc();
        this.length = this.dbMediaType.getLength();
        this.setTable(table);
        this.setAlias(alias);
    }


}
