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
public interface Wrapper<P, Result>  {

    Result eq(boolean condition, P column, Object val);
    default Result eq(P column, Object val) {
        return eq(true, column, val);
    }

    Result ge(boolean condition, P column, Object val);
    default Result ge(P column, Object val) {
        return ge(true, column, val);
    }

    Result le(boolean condition, P column, Object val);
    default Result le(P column, Object val) {
        return le(true, column, val);
    }

    Result lt(boolean condition, P column, Object val);
    default Result lt(P column, Object val) {
        return lt(true, column, val);
    }

    Result gt(boolean condition, P column, Object val);
    default Result gt(P column, Object val) {
        return gt(true, column, val);
    }

    Result in(boolean condition, P column, Collection<? extends Serializable> val);
    default Result in(P column, Collection<? extends Serializable> val) {
        return in(true, column, val);
    }

    Result notIn(boolean condition, P column, Collection<? extends Serializable> val);
    default Result notIn(P column, Collection<? extends Serializable> val) {
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

    Result like(boolean condition, P column, Object val);
    default Result like(P column, Object val) {
        return like(true, column, val);
    }

    Result notLike(boolean condition, P column, Object val);
    default Result notLike(P column, Object val) {
        return notLike(true, column, val);
    }

    Result likeLeft(boolean condition, P column, Object val);
    default Result likeLeft(P column, Object val) {
        return likeLeft(true, column, val);
    }

    Result likeRight(boolean condition, P column, Object val);
    default Result likeRight(P column, Object val) {
        return likeRight(true, column, val);
    }

    Result between(boolean condition, P column, Object val1, Object val2);
    default Result between(P column, Object val1, Object val2) {
        return between(true, column, val1, val2);
    }

    Result notBetween(boolean condition, P column, Object val1, Object val2);
    default Result notBetween(P column, Object val1, Object val2) {
        return notBetween(true, column, val1, val2);
    }

    Result isNull(boolean condition, P column);
    default Result isNull(P column) {
        return isNull(true, column);
    }

    Result isNotNull(boolean condition, P column);
    default Result isNotNull(P column) {
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

    Result orderByAsc(boolean condition, P... columns);

    @SuppressWarnings("unchecked")
    default Result orderByAsc(P... columns) {
        return orderByAsc(true, columns);
    }


    Result orderByDesc(boolean condition, P... columns);
    default Result orderByDesc(P... columns) {
        return orderByDesc(true, columns);
    }


}
