package com.custom.action.enums;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/10 0010 21:36
 * @Version 1.0
 * @Description DbMediaType
 */
public enum DbMediaType {

    DbInt("int", "11"),

    DbVarchar("varchar", "50"),

    DbFloat("float", "11"),

    DbTimestamp("timestamp", "11"),

    DbDecimal("decimal", "16,2"),

    DbDouble("double", "12,2"),

    DbTinyint("tinyint", "1"),

    DbBit("bit", "1"),

    DbText("text", "255"),

    DbBigint("bigint", "11"),

    DbDate("date", "0"),

    DbDateTime("datetime", "0");

    private final String type;
    private final String length;

    DbMediaType(String type, String length) {
        this.type = type;
        this.length = length;
    }

    public String getLength() {
        return length;
    }

    public String getType() {
        return type;
    }
}
