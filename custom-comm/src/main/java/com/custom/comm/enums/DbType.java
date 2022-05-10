package com.custom.comm.enums;

import com.custom.comm.exceptions.CustomCheckException;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/10 0010 21:36
 * @Version 1.0
 * @Description DbMediaType
 */
public enum DbType {

    DbInt("int", Integer.class, "11"),

    DbVarchar("varchar", String.class, "50"),

    DbFloat("float", Float.class, "11"),

    DbDecimal("decimal", BigDecimal.class, "16,2"),

    DbDouble("double", Double.class, "12,2"),

    DbTinyint("tinyint", Integer.class,  "1"),

    DbBit("bit", Boolean.class, "1"),

    DbText("text", String.class,  "255"),

    DbBigint("bigint", Long.class,  "11"),

    DbDate("date", Date.class,  "0"),

    DbDateTime("datetime", Date.class, "0");

    private final String type;
    private final Class<?> fieldType;
    private final String length;

    DbType(String type, Class<?> fieldType, String length) {
        this.type = type;
        this.fieldType = fieldType;
        this.length = length;
    }

    public static DbType getDbMediaType(Class<?> type) {
        for (DbType value : values()) {
            if(value.fieldType.equals(type)) {
                return value;
            }
        }
        throw new CustomCheckException("找不到匹配的类型");
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
