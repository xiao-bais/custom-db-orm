package com.custom.action.core;

import com.custom.action.dbaction.AbstractTableModel;
import com.custom.action.util.DbUtil;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.enums.DbType;
import com.custom.comm.enums.KeyStrategy;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.AssertUtil;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.jdbc.configuration.GlobalDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * 对于@DbKey的字段解析
 * @author   Xiao-Bai
 * @since  2021/12/2 0:44
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

    @Override
    public Object getValue(T t) {
        AssertUtil.npe(t);
        try {
            return getFieldValue(t, key);
        }catch (InvocationTargetException | IllegalAccessException
                | NoSuchMethodException | NoSuchFieldException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
    * 生成主键
    */
    protected Object generateKey(T currEntity) {
        Object key = null;
        switch (strategy) {
            case AUTO:
                if(type.equals(Integer.class) || type.equals(Integer.TYPE)) {
                    key = 0;
                }else if(type.equals(Long.class) || type.equals(Long.TYPE)) {
                    key = 0L;
                }
                break;
            case UUID:
                key = CustomUtil.getUUID();
                this.setValue(currEntity, key);
                break;
            case INPUT:
                key = this.getValue(currEntity);
                if(key == null) {
                    throw new CustomCheckException("The value of the primary key cannot be null");
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

    @Override
    protected String getSelectAsFieldSql() {
        return DbUtil.sqlSelectAsWrapper(DbUtil.fullSqlColumn(this.getAlias(), this.dbKey), this.key);
    }

    @Override
    protected void setValue(T obj, Object value) {
        super.setFieldValue(obj, this.field, value);
    }


    @Override
    public String createTableSql() {
        StringBuilder keyFieldSql = new StringBuilder(String.format("`%s` ", this.dbKey));
        keyFieldSql.append(this.dbType.getType())
                .append(Constants.BRACKETS_LEFT)
                .append(this.length)
                .append(Constants.BRACKETS_RIGHT);

        if(this.strategy == KeyStrategy.AUTO) {
            keyFieldSql.append(" auto_increment ");
        }
        keyFieldSql.append(" NOT NULL ").append(String.format(" COMMENT '%s'", this.desc));
        return keyFieldSql.toString();
    }


    public DbKeyParserModel(Field field, String table, String alias, boolean underlineToCamel){
        this.key = GlobalDataHandler.hasSqlKeyword(field.getName()) ? String.format("`%s`", field.getName()) : field.getName();
        DbKey annotation = field.getAnnotation(DbKey.class);
        if (JudgeUtil.isEmpty(annotation.value())) {
            this.dbKey = underlineToCamel ? CustomUtil.camelToUnderline(this.key) : this.key;
        }else {
            this.dbKey = annotation.value();
        }
        this.dbKey = GlobalDataHandler.hasSqlKeyword(this.dbKey) ? String.format("`%s`", this.dbKey) : this.dbKey;
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
