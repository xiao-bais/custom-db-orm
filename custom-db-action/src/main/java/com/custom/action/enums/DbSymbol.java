package com.custom.action.enums;

import com.custom.comm.SymbolConst;

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
    EXISTS("exists"),
    NOT_EXISTS("not exists"),
    BETWEEN("between ? and ?"),
    NOT_BETWEEN("not between ? and ?"),
    AND(SymbolConst.AND),
    OR(SymbolConst.OR),
    LIKE("like"),
    NOT_LIKE("not like"),
    IS_NOT_NULL("is not null"),
    IS_NULL("is null"),
    NOT_IN("not in"),
    ORDER_BY("order by"),
    IN("in"),
    SELECT("select"),
    GROUP_BY("group by"),
    HAVING("having");



    private final String symbol;

    DbSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
