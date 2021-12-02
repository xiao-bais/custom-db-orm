package com.custom.sqlparser;

import com.custom.annotations.DbKey;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.DbMediaType;
import com.custom.enums.KeyStrategy;
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

    public Object getValue() {
        return value;
    }

    @Override
    public Object getValue(T t, String fieldName) {
        try {
            this.value = getFieldValue(t, fieldName);
        }catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
        }
        return value;
    }

    @Override
    public String getFieldSql() {
        return String.format("%s.`%s`", super.getAlias(), this.dbKey);
    }

    @Override
    public String getSelectFieldSql() {
        return String.format("%s.`%s` `%s`", super.getAlias(), this.dbKey, this.key);
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String buildTableSql() {

        StringBuilder keyFieldSql = new StringBuilder(String.format("`%s` ", this.dbKey));
        keyFieldSql.append(this.dbMediaType.getType())
                .append(SymbolConst.BRACKETS_LEFT)
                .append(this.dbMediaType.getLength())
                .append(SymbolConst.BRACKETS_RIGHT).append(" ");

        if(this.strategy == KeyStrategy.AUTO)
            keyFieldSql.append("AUTO_INCREMENT ");

        keyFieldSql.append("NOT NULL").append(" ")
                .append(String.format("COMMENT '%s'", this.desc));
        return keyFieldSql.toString();
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
        super.setTable(table);
        super.setAlias(alias);
    }

    public DbKeyParserModel(T t, Field field, String table, String alias){
        this.key = field.getName();
        DbKey annotation = field.getAnnotation(DbKey.class);
        this.dbKey = JudgeUtilsAx.isEmpty(annotation.value()) ? this.key : annotation.value();
        this.type = field.getType();
        this.dbMediaType = annotation.dbType();
        this.strategy = annotation.strategy();
        this.desc = annotation.desc();
        this.length = this.dbMediaType.getLength();
        super.setTable(table);
        super.setAlias(alias);
        try {
            this.value = getFieldValue(t, this.key);
        }catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
