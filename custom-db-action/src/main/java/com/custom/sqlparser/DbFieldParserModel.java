package com.custom.sqlparser;

import com.custom.annotations.DbField;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.DbMediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Xiao-Bai
 * @date 2021/12/1 23:39
 * @desc: 对于@DbField注解的解析
 */
@SuppressWarnings("unchecked")
public class DbFieldParserModel<T> {

    private static Logger logger = LoggerFactory.getLogger(DbFieldParserModel.class);

    private String fieldName;

    private String column;

    private DbMediaType dbMediaType;

    private Class<?> type;

    private Object value;

    private String length;

    private String desc;

    private boolean isNull;


    /**
    * 构建创建表的sql语句
    */
    public String buildTableSql(){
        StringBuilder fieldSql = new StringBuilder(String.format("`%s` ", this.column));
        if(dbMediaType == DbMediaType.DbDate || dbMediaType == DbMediaType.DbDateTime) {
            fieldSql.append(dbMediaType.getType()).append(" ");
        }else
            fieldSql.append(dbMediaType.getType()).append(" ")
                    .append(SymbolConst.BRACKETS_LEFT)
                    .append(this.length)
                    .append(SymbolConst.BRACKETS_RIGHT).append(" ");

        fieldSql.append(this.isNull ? "NULL" : " NOT NULL").append(" ");
        fieldSql.append(String.format(" COMMENT '%s'", this.desc));
        return fieldSql.toString();
    }


    public DbFieldParserModel(Field field) {
        this.fieldName = field.getName();
        this.type = field.getType();
        DbField annotation = field.getAnnotation(DbField.class);
        this.column = JudgeUtilsAx.isEmpty(annotation.value()) ? this.fieldName : annotation.value();
        this.isNull = annotation.isNull();
        this.desc = annotation.desc();
        this.dbMediaType = annotation.fieldType();
        this.length = this.dbMediaType.getLength();
    }

    public DbFieldParserModel(T t, Field field) {
        this.fieldName = field.getName();
        this.type = field.getType();
        DbField annotation = field.getAnnotation(DbField.class);
        this.column = JudgeUtilsAx.isEmpty(annotation.value()) ? this.fieldName : annotation.value();
        this.isNull = annotation.isNull();
        this.desc = annotation.desc();
        this.dbMediaType = annotation.fieldType();
        this.length = this.dbMediaType.getLength();
        try {
            this.value = getFieldValue(t, this.fieldName);
        }catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
        }
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

    public Object getValue(T x, String fieldName) {
        if(JudgeUtilsAx.isEmpty(value)) {
            try {
                value = getFieldValue(x, fieldName);
            }catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }
        return value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
    * E: 实例对象
    * fieldName: 字段名称
    */
    Object getFieldValue(T x, String fieldName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Object value;
        String firstLetter;
        String getter;
        try {
            firstLetter = fieldName.substring(0, 1).toUpperCase();
            getter = SymbolConst.GET + firstLetter + fieldName.substring(1);
            Method method = x.getClass().getMethod(getter);
            value = method.invoke(x);
        }catch (NoSuchMethodException e){
            try {
                firstLetter = fieldName.substring(0, 1).toUpperCase();
                Method method = x.getClass().getMethod(SymbolConst.IS + firstLetter + fieldName.substring(1));
                value = method.invoke(x);
            }catch (NoSuchMethodException v) {
                Method method = x.getClass().getMethod(fieldName);
                value = method.invoke(x);
            }
        }
        return value;
    }


}
