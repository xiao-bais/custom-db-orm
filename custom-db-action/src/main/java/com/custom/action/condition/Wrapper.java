package com.custom.action.condition;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 条件包装
 * @param <Param> 字段类型
 * @param <Result> 返回结果类型
 * @author  Xiao-Bai
 * @since  2022/2/16 14:01
 **/
@SuppressWarnings("all")
public interface Wrapper<Param, Result>  {

    Result eq(boolean condition, Param column, Object val);
    default Result eq(Param column, Object val) {
        return eq(true, column, val);
    }

    Result ne(boolean condition, Param column, Object val);
    default Result ne(Param column, Object val) {
        return ne(true, column, val);
    }

    Result ge(boolean condition, Param column, Object val);
    default Result ge(Param column, Object val) {
        return ge(true, column, val);
    }

    Result le(boolean condition, Param column, Object val);
    default Result le(Param column, Object val) {
        return le(true, column, val);
    }

    Result lt(boolean condition, Param column, Object val);
    default Result lt(Param column, Object val) {
        return lt(true, column, val);
    }

    Result gt(boolean condition, Param column, Object val);
    default Result gt(Param column, Object val) {
        return gt(true, column, val);
    }

    Result in(boolean condition, Param column, Collection<?> val);
    default Result in(Param column, Collection<?> val) {
        return in(true, column, val);
    }

    default Result in(boolean condition, Param column, Object... values) {
        return in(condition, column,
                Arrays.stream(Optional.ofNullable(values).orElseGet(() -> new Object[]{}))
                        .collect(Collectors.toList())
        );
    }
    default Result in(Param column, Object... values) {
        return in(true, column, values);
    }

    Result inSql(boolean condition, Param column, String inSql, Object... params);
    default Result inSql(Param column, String inSql, Object... params) {
       return inSql(true, column, inSql, params);
    }

    Result notIn(boolean condition, Param column, Collection<?> val);
    default Result notIn(Param column, Collection<?> val) {
        return notIn(true, column, val);
    }

    Result notInSql(boolean condition, Param column, String inSql, Object... params);
    default Result notInSql(Param column, String inSql, Object... params) {
        return notInSql(true, column, inSql, params);
    }

    Result exists(boolean condition, String existsSql);
    default Result exists(String existsSql) {
        return exists(true, existsSql);
    }

    Result notExists(boolean condition, String notExistsSql);
    default Result notExists(String notExistsSql) {
        return notExists(true, notExistsSql);
    }

    Result like(boolean condition, Param column, Object val);
    default Result like(Param column, Object val) {
        return like(true, column, val);
    }

    Result notLike(boolean condition, Param column, Object val);
    default Result notLike(Param column, Object val) {
        return notLike(true, column, val);
    }

    Result likeLeft(boolean condition, Param column, Object val);
    default Result likeLeft(Param column, Object val) {
        return likeLeft(true, column, val);
    }

    Result likeRight(boolean condition, Param column, Object val);
    default Result likeRight(Param column, Object val) {
        return likeRight(true, column, val);
    }

    Result between(boolean condition, Param column, Object val1, Object val2);
    default Result between(Param column, Object val1, Object val2) {
        return between(true, column, val1, val2);
    }

    Result notBetween(boolean condition, Param column, Object val1, Object val2);
    default Result notBetween(Param column, Object val1, Object val2) {
        return notBetween(true, column, val1, val2);
    }

    Result isNull(boolean condition, Param column);
    default Result isNull(Param column) {
        return isNull(true, column);
    }

    Result isNotNull(boolean condition, Param column);
    default Result isNotNull(Param column) {
        return isNotNull(true, column);
    }


}
