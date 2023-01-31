package com.custom.comm.enums;

import com.custom.comm.utils.Constants;
import com.custom.comm.exceptions.CustomCheckException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/10 0010 21:36
 * @Version 1.0
 * @Description DbMediaType
 */
public enum DbType {

    DbInt("int", Integer.class, Integer.TYPE, "11", 0),

    DbVarchar("varchar", String.class, null, "64", Constants.EMPTY),

    DbChar("char", Character.class, Character.TYPE, "6", Constants.EMPTY),

    DbFloat("float", Float.class, Float.TYPE,  "11", 0.0F),

    DbDecimal("decimal", BigDecimal.class, null, "12,4", BigDecimal.ZERO),

    DbDouble("double", Double.class, Double.TYPE, "5,2", 0.0D),

    DbTinyint("tinyint", Integer.class, Integer.TYPE, "1", 0),

    DbBit("bit", Boolean.class, Boolean.TYPE, "1", false),

    DbText("text", String.class, null,  "255", Constants.EMPTY),

    DbBigint("bigint", Long.class, Long.TYPE,  "11", 0L),

    DbDate("date", Date.class,  null, "0", null),

    DbDateTime("datetime", Date.class, null, "0", null);

    /**
     * 数据库类型
     */
    private final String type;
    /**
     * java属性类型
     */
    private final Class<?> fieldType;
    /**
     * java属性类型对应的基础类型，若不存在，则为null
     */
    private final Class<?> baseType;
    /**
     * 字段长度
     */
    private final String length;
    /**
     * 字段默认值
     */
    private final Object value;

    DbType(String type, Class<?> fieldType, Class<?> baseType, String length, Object value) {
        this.type = type;
        this.fieldType = fieldType;
        this.baseType = baseType;
        this.length = length;
        this.value = value;
    }

    public static DbType getDbMediaType(Class<?> type) {
        for (DbType value : values()) {
            if(value.fieldType.equals(type)
                    || (Objects.nonNull(value.baseType) && value.baseType.equals(type))) {
                return value;
            }
        }
        throw new CustomCheckException(type.getName() + " 找不到匹配的类型");
    }

    public static DbType getDbType(String type) {
        for (DbType value : values()) {
            if(value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

    public String getLength() {
        return length;
    }

    public String getType() {
        return type;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public Object getValue() {
        return value;
    }
}
