package com.custom.joiner.core.condition;

import com.custom.comm.utils.lambda.SFunction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author  Xiao-Bai
 * @since  2022/8/31 23:46
 * @desc
 */
public interface JoinConditionWrapper<Result> {


    <Param> Result eq(boolean condition, SFunction<Param, ?> column, Object val);
    default <Param>  Result eq(SFunction<Param, ?> column, Object val) {
        return eq(true, column, val);
    }

    <Param>  Result ge(boolean condition, SFunction<Param, ?> column, Object val);
    default <Param>  Result ge(SFunction<Param, ?> column, Object val) {
        return ge(true, column, val);
    }

    <Param>  Result le(boolean condition, SFunction<Param, ?> column, Object val);
    default <Param>  Result le(SFunction<Param, ?> column, Object val) {
        return le(true, column, val);
    }

    <Param>  Result lt(boolean condition, SFunction<Param, ?> column, Object val);
    default <Param>  Result lt(SFunction<Param, ?> column, Object val) {
        return lt(true, column, val);
    }

    <Param>  Result gt(boolean condition, SFunction<Param, ?> column, Object val);
    default <Param> Result gt(SFunction<Param, ?> column, Object val) {
        return gt(true, column, val);
    }

    <Param> Result in(boolean condition, SFunction<Param, ?> column, Collection<?> val);
    default <Param> Result in(SFunction<Param, ?> column, Collection<?> val) {
        return in(true, column, val);
    }

    default <Param> Result in(boolean condition, SFunction<Param, ?> column, Object... values) {
        return in(condition, column,
                Arrays.stream(Optional.ofNullable(values).orElseGet(() -> new Object[]{}))
                        .collect(Collectors.toList())
        );
    }
    default <Param> Result in(SFunction<Param, ?> column, Object... values) {
        return in(true, column, values);
    }

    <Param> Result inSql(boolean condition, SFunction<Param, ?> column, String inSql, Object... params);
    default <Param> Result inSql(SFunction<Param, ?> column, String inSql, Object... params) {
        return inSql(true, column, inSql, params);
    }

    <Param>  Result notIn(boolean condition, SFunction<Param, ?> column, Collection<?> val);
    default <Param> Result notIn(SFunction<Param, ?> column, Collection<?> val) {
        return notIn(true, column, val);
    }

    <Param> Result notInSql(boolean condition, SFunction<Param, ?> column, String inSql, Object... params);
    default <Param> Result notInSql(SFunction<Param, ?> column, String inSql, Object... params) {
        return notInSql(true, column, inSql, params);
    }

    <Param> Result exists(boolean condition, String existsSql);
    default <Param> Result exists(String existsSql) {
        return exists(true, existsSql);
    }

    <Param> Result notExists(boolean condition, String notExistsSql);
    default <Param> Result notExists(String notExistsSql) {
        return notExists(true, notExistsSql);
    }

    <Param> Result like(boolean condition, SFunction<Param, ?> column, Object val);
    default <Param> Result like(SFunction<Param, ?> column, Object val) {
        return like(true, column, val);
    }

    <Param> Result notLike(boolean condition, SFunction<Param, ?> column, Object val);
    default <Param> Result notLike(SFunction<Param, ?> column, Object val) {
        return notLike(true, column, val);
    }

    <Param> Result likeLeft(boolean condition, SFunction<Param, ?> column, Object val);
    default <Param> Result likeLeft(SFunction<Param, ?> column, Object val) {
        return likeLeft(true, column, val);
    }

    <Param> Result likeRight(boolean condition, SFunction<Param, ?> column, Object val);
    default <Param> Result likeRight(SFunction<Param, ?> column, Object val) {
        return likeRight(true, column, val);
    }

    <Param> Result between(boolean condition, SFunction<Param, ?> column, Object val1, Object val2);
    default <Param> Result between(SFunction<Param, ?> column, Object val1, Object val2) {
        return between(true, column, val1, val2);
    }

    <Param> Result notBetween(boolean condition, SFunction<Param, ?> column, Object val1, Object val2);
    default <Param> Result notBetween(SFunction<Param, ?> column, Object val1, Object val2) {
        return notBetween(true, column, val1, val2);
    }

    <Param> Result isNull(boolean condition, SFunction<Param, ?> column);
    default <Param> Result isNull(SFunction<Param, ?> column) {
        return isNull(true, column);
    }

    <Param> Result isNotNull(boolean condition, SFunction<Param, ?> column);
    default <Param> Result isNotNull(SFunction<Param, ?> column) {
        return isNotNull(true, column);
    }

}
