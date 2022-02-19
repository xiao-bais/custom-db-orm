package com.custom.wrapper;

import java.io.Serializable;
import java.util.Collection;

/**
 * @Author Xiao-Bai
 * @Date 2022/2/16 14:01
 * @Desc：顶级条件构造器
 * column: 表字段
 **/
public interface Wrapper<T, Result>  {


    Result eq(boolean condition, T column, Object val);
    default Result eq(T column, Object val) {
        return eq(true, column, val);
    }

    Result ge(boolean condition, T column, Object val);
    default Result ge(T column, Object val) {
        return ge(true, column, val);
    }

    Result le(boolean condition, T column, Object val);
    default Result le(T column, Object val) {
        return le(true, column, val);
    }

    Result lt(boolean condition, T column, Object val);
    default Result lt(T column, Object val) {
        return lt(true, column, val);
    }

    Result gt(boolean condition, T column, Object val);
    default Result gt(T column, Object val) {
        return gt(true, column, val);
    }

    Result in(boolean condition, T column, Collection<? extends Serializable> val);
    default Result in(T column, Collection<? extends Serializable> val) {
        return in(true, column, val);
    }

    Result notIn(boolean condition, T column, Collection<? extends Serializable> val);
    default Result notIn(T column, Collection<? extends Serializable> val) {
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

    Result like(boolean condition, T column, Object val);
    default Result like(T column, Object val) {
        return like(true, column, val);
    }

    Result notLike(boolean condition, T column, Object val);
    default Result notLike(T column, Object val) {
        return notLike(true, column, val);
    }

    Result likeLeft(boolean condition, T column, Object val);
    default Result likeLeft(T column, Object val) {
        return likeLeft(true, column, val);
    }

    Result likeRight(boolean condition, T column, Object val);
    default Result likeRight(T column, Object val) {
        return likeRight(true, column, val);
    }

    Result between(boolean condition, T column, Object val1, Object val2);
    default Result between(T column, Object val1, Object val2) {
        return between(true, column, val1, val2);
    }

    Result notBetween(boolean condition, T column, Object val1, Object val2);
    default Result notBetween(T column, Object val1, Object val2) {
        return notBetween(true, column, val1, val2);
    }

    Result isNull(boolean condition, T column);
    default Result isNull(T column) {
        return isNull(true, column);
    }

    Result isNotNull(boolean condition, T column);
    default Result isNotNull(T column) {
        return isNotNull(true, column);
    }

    Result or(boolean condition, Result conditionEntity);
    default Result or(Result conditionEntity) {
        return or(true, conditionEntity);
    }

    Result and(boolean condition, Result conditionEntity);
    default Result and(Result conditionEntity) {
        return and(true, conditionEntity);
    }

    Result orderByAsc(boolean condition, T column);
    default Result orderByAsc(T column) {
        return orderByAsc(true, column);
    }

    Result orderByAsc(boolean condition, T... columns);
    default Result orderByAsc(T... columns) {
        return orderByAsc(true, columns);
    }

    Result orderByDesc(boolean condition, T column);
    default Result orderByDesc(T column) {
        return orderByDesc(true, column);
    }

    Result orderByDesc(boolean condition, T... columns);
    default Result orderByDesc(T... columns) {
        return orderByDesc(true, columns);
    }

}
