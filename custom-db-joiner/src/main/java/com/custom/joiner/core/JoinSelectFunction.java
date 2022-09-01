package com.custom.joiner.core;

import com.custom.action.condition.SFunction;
import com.custom.joiner.core.func.AbstractJoinFunction;

/**
 * @Author Xiao-Bai
 * @Date 2022/9/1 0001 11:01
 * @Desc
 */
public class JoinSelectFunction extends AbstractJoinFunction<JoinSelectFunction> {


    @Override
    public <A> JoinSelectFunction sum(boolean isNullToZero, SFunction<A, ?> column) {
        return null;
    }

    @Override
    public <A> JoinSelectFunction avg(boolean isNullToZero, SFunction<A, ?> column) {
        return null;
    }

    @Override
    public <R> JoinSelectFunction count(SFunction<R, ?> column, boolean distinct) {
        return null;
    }

    @Override
    public <A> JoinSelectFunction ifNull(SFunction<A, ?> column, Object elseVal) {
        return null;
    }

    @Override
    public <A> JoinSelectFunction max(boolean isNullToZero, SFunction<A, ?> column) {
        return null;
    }

    @Override
    public <A> JoinSelectFunction min(boolean isNullToZero, SFunction<A, ?> column) {
        return null;
    }
}
