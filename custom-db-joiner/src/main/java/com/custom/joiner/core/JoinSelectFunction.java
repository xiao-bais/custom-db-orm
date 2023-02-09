package com.custom.joiner.core;

import com.custom.comm.utils.lambda.SFunction;
import com.custom.joiner.core.func.AbstractJoinFunction;

/**
 * @author  Xiao-Bai
 * @since  2022/9/1 0001 11:01
 * @Desc
 */
public class JoinSelectFunction<T> extends AbstractJoinFunction<JoinSelectFunction<T>> {


    @Override
    public <A> JoinSelectFunction<T> sum(boolean isNullToZero, SFunction<A, ?> column) {
        return null;
    }

    @Override
    public <A> JoinSelectFunction<T> avg(boolean isNullToZero, SFunction<A, ?> column) {
        return null;
    }

    @Override
    public <R> JoinSelectFunction<T> count(SFunction<R, ?> column, boolean distinct) {
        return null;
    }

    @Override
    public <A> JoinSelectFunction<T> ifNull(SFunction<A, ?> column, Object elseVal) {
        return null;
    }

    @Override
    public <A> JoinSelectFunction<T> max(boolean isNullToZero, SFunction<A, ?> column) {
        return null;
    }

    @Override
    public <A> JoinSelectFunction<T> min(boolean isNullToZero, SFunction<A, ?> column) {
        return null;
    }
}
