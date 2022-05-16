package com.custom.comm.enums;

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

    DbInt("int", Integer.class, Integer.TYPE, "11"),

    DbVarchar("varchar", String.class, null, "50"),

    DbFloat("float", Float.class, Float.TYPE,  "11"),

    DbDecimal("decimal", BigDecimal.class, null, "16,2"),

    DbDouble("double", Double.class, Double.TYPE, "12,2"),

    DbTinyint("tinyint", Integer.class, Integer.TYPE, "1"),

    DbBit("bit", Boolean.class, Boolean.TYPE, "1"),

    DbText("text", String.class, null,  "255"),

    DbBigint("bigint", Long.class, Long.TYPE,  "11"),

    DbDate("date", Date.class,  null, "0"),

    DbDateTime("datetime", Date.class, null, "0");

    private final String type;
    private final Class<?> fieldType;
    private final Class<?> baseType;
    private final String length;

    DbType(String type, Class<?> fieldType, Class<?> baseType, String length) {
        this.type = type;
        this.fieldType = fieldType;
        this.baseType = baseType;
        this.length = length;
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
}
