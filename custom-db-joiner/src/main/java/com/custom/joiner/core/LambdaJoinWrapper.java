package com.custom.joiner.core;

import com.custom.action.condition.SFunction;
import com.custom.joiner.condition.AbstractJoinConditional;
import com.custom.joiner.condition.LambdaJoinConditional;

import java.util.function.Consumer;

/**
 * @author Xiao-Bai
 * @date 2022/8/29 14:59
 * @desc
 */
public class LambdaJoinWrapper<T> implements JoinWrapper<T> {


    @Override
    public <A, B> LambdaJoinWrapper<T> leftJoin(Class<A> aClass, Class<B> bClass, SFunction<A, ?> aColumn, SFunction<B, ?> bColumn) {
        return null;
    }

    @Override
    public <B> LambdaJoinWrapper<T> leftJoin(Class<B> bClass, SFunction<T, ?> aColumn, SFunction<B, ?> bColumn) {
        return null;
    }

    @Override
    public <B> LambdaJoinWrapper<T> leftJoin(Class<B> bClass, AbstractJoinConditional<T, B> joinConditional) {
        return null;
    }

    @Override
    public <B> LambdaJoinWrapper<T> leftJoin(Class<B> bClass, Consumer<AbstractJoinConditional<T, B>> joinConditional) {
        return null;
    }
}
