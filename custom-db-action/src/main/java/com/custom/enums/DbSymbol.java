package com.custom.enums;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/11 13:30
 * @Desc：sql的条件符号
 **/
public enum DbSymbol {

    EQUALS("="),
    NOT_EQUALS("<>"),
    GREATER_THAN(">"),
    GREATER_THAN_EQUALS(">="),
    LESS_THAN("<"),
    LESS_THAN_EQUALS("<="),
    EXISTS("EXISTS"),
    NOT_EXISTS("NOT EXISTS"),
    BETWEEN("BETWEEN %s AND %s"),
    NOT_BETWEEN("NOT BETWEEN %s AND %s"),
    AND("AND"),
    GROUP_BY("GROUP BY"),
    HAVING("HAVING"),
    OR("OR"),
    LIKE("LIKE"),
    IS_NOT_NULL("IS NOT NULL"),
    IS_NULL("IS NULL"),
    NOT_IN("NOT IN"),
    ORDER_BY("ORDER BY"),
    IN("IN");



    private String symbol;

    DbSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
