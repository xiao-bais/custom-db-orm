package com.custom.wrapper;

import java.io.Serializable;
import java.util.Collection;

/**
 * @Author Xiao-Bai
 * @Date 2022/2/16 14:01
 * @Desc：顶级条件构造器
 * column: 表字段
 **/
@SuppressWarnings("unchecked")
public interface Wrapper<Param, Result>  {

    Result eq(boolean condition, Param column, Object val);
    default Result eq(Param column, Object val) {
        return eq(true, column, val);
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

    Result in(boolean condition, Param column, Collection<? extends Serializable> val);
    default Result in(Param column, Collection<? extends Serializable> val) {
        return in(true, column, val);
    }

    Result notIn(boolean condition, Param column, Collection<? extends Serializable> val);
    default Result notIn(Param column, Collection<? extends Serializable> val) {
        return notIn(true, column, val);
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

    Result orderByAsc(boolean condition, Param... columns);

    @SuppressWarnings("all")
    default Result orderByAsc(Param... columns) {
        return orderByAsc(true, columns);
    }


    @SuppressWarnings("all")
    Result orderByDesc(boolean condition, Param... columns);
    default Result orderByDesc(Param... columns) {
        return orderByDesc(true, columns);
    }


}
