package com.custom.wrapper;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.DbSymbol;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Stream;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/13 9:23
 * @Desc：条件构造器抽象接口
 **/
public abstract class AbstractWrapper<T, Children> {


    public abstract Children adapter(DbSymbol dbSymbol, String column, Object val);
    public abstract Children adapter(DbSymbol dbSymbol, boolean condition, String column, Object val);
    public abstract Children adapter(DbSymbol dbSymbol, boolean condition, String column, Object val1, Object val2);
    public abstract Children adapter(DbSymbol dbSymbol, boolean condition, String column, Object val, String express);


    @SuppressWarnings("unchecked")
    public Class<T> getTClass(){
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
    * 适配各种sql条件的拼接
    */
    public StringBuilder adapterCondition(DbSymbol dbSymbol, boolean condition, String column, Object val1, Object val2, String express) {

        if(!condition) {
            return null;
        }

        switch (dbSymbol) {
            case EQUALS:
            case NOT_EQUALS:
            case LESS_THAN:
            case GREATER_THAN:
            case LESS_THAN_EQUALS:
            case GREATER_THAN_EQUALS:
                conditional.append(String.format(" and %s %s ?", column, dbSymbol.getSymbol()));
                paramValues.add(val1);
                break;
            case LIKE:
            case NOT_LIKE:
                conditional.append(String.format(" and %s %s %s", column, dbSymbol.getSymbol(), express));
                paramValues.add(val1);
                break;
            case IN:
            case NOT_IN:
                StringJoiner symbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
                if(CustomUtil.isBasicType(val1)) {
                    paramValues.add(val1);

                }else if(val1.getClass().isArray()) {
                    int len = Array.getLength(val1);
                    for (int i = 0; i < len; i++) {
                        symbol.add(SymbolConst.QUEST);
                        paramValues.add(Array.get(val1, i));
                    }

                }else if(val1 instanceof Collection) {
                    Collection<?> objects = (Collection<?>) val1;
                    paramValues.addAll(objects);
                    objects.forEach(x -> symbol.add(SymbolConst.QUEST));
                }
                conditional.append(String.format(" and %s %s (%s)", column, dbSymbol.getSymbol(), symbol));
                break;
            case EXISTS:
            case NOT_EXISTS:
                conditional.append(String.format(" and %s %s", dbSymbol.getSymbol(), val1));
                break;
            case BETWEEN:
            case NOT_BETWEEN:
                if(!CustomUtil.isBasicType(val1.getClass()) || !CustomUtil.isBasicType(val2.getClass())) {
                    throw new IllegalArgumentException("val1 or val2 can only be basic types");
                }
                if(JudgeUtilsAx.isEmpty(val1) || JudgeUtilsAx.isEmpty(val2)) {
                    throw new NullPointerException("At least one null value exists between val1 and val2");
                }
                conditional.append(String.format(" and %s " + dbSymbol.getSymbol(), column, val1, val2));
                paramValues.add(val1);
                paramValues.add(val2);
                break;
            case IS_NULL:
            case IS_NOT_NULL:
                conditional.append(String.format(" and %s %s", column, dbSymbol.getSymbol()));
                break;
            case ORDER_BY:
                conditional.append(String.format("\n%s %s", dbSymbol.getSymbol(), val1));
        }


        return conditional;
    }


    private StringBuilder conditional = new StringBuilder();

    private List<Object> paramValues = new ArrayList<>();

    public List<Object> getParamValues() {
        return paramValues;
    }

    public void setParamValues(List<Object> paramValues) {
        this.paramValues = paramValues;
    }


    public StringBuilder getConditional() {
        return conditional;
    }

    public String and(String condition) {
        if(condition.trim().startsWith(DbSymbol.AND.getSymbol())) {
            condition = condition.replaceFirst(DbSymbol.AND.getSymbol(), SymbolConst.EMPTY);
        }
        return String.format(" and (%s)", condition);
    }
}
