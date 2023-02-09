package com.custom.comm.enums;

import com.custom.comm.utils.Constants;

/**
 * sql的条件符号
 * @author  Xiao-Bai
 * @since 2021/12/11 13:30
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
    BETWEEN("BETWEEN ? AND ?"),
    NOT_BETWEEN("NOT BETWEEN ? AND ?"),
    AND(Constants.AND),
    OR(Constants.OR),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    IS_NOT_NULL("IS NOT NULL"),
    IS_NULL("IS NULL"),
    IN("IN"),
    NOT_IN("NOT IN"),
    ORDER_BY(Constants.ORDER_BY),
    ORDER_BY_ASC(Constants.ASC),
    ORDER_BY_DESC(Constants.ASC),
    SELECT("SELECT"),
    GROUP_BY(Constants.GROUP_BY),
    HAVING(Constants.HAVING);



    private final String symbol;

    DbSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
