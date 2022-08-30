package com.custom.joiner.condition;

import com.custom.action.condition.SFunction;

import java.util.Collection;

/**
 * @author Xiao-Bai
 * @date 2022/8/29 14:11
 * @desc
 */
public class LambdaJoinConditional<A, B> extends AbstractJoinConditional<A, B> {
    @Override
    public LambdaJoinConditional<A, B> alias(String joinAlias) {
        return null;
    }

    @Override
    public LambdaJoinConditional<A, B> eq(SFunction<A, ?> aColumn, SFunction<B, ?> bColumn) {
        return applyCondition(() ->
                String.format("a.%s = b.%s",
                        toAColumn(aColumn), toBColumn(bColumn))
        );
    }

    @Override
    public LambdaJoinConditional<A, B> eq(SFunction<B, ?> bColumn, Object val) {
        return null;
    }

    @Override
    public LambdaJoinConditional<A, B> gt(SFunction<B, ?> bColumn, Object val) {
        return null;
    }

    @Override
    public LambdaJoinConditional<A, B> ge(SFunction<B, ?> bColumn, Object val) {
        return null;
    }

    @Override
    public LambdaJoinConditional<A, B> lt(SFunction<B, ?> bColumn, Object val) {
        return null;
    }

    @Override
    public LambdaJoinConditional<A, B> le(SFunction<B, ?> bColumn, Object val) {
        return null;
    }

    @Override
    public LambdaJoinConditional<A, B> between(SFunction<B, ?> bColumn, Object val1, Object val2) {
        return null;
    }

    @Override
    public LambdaJoinConditional<A, B> in(SFunction<B, ?> bColumn, Object... values) {
        return null;
    }

    @Override
    public LambdaJoinConditional<A, B> in(SFunction<B, ?> bColumn, Collection<?> val) {
        return null;
    }

    public LambdaJoinConditional(Class<A> aClass, Class<B> bClass) {
        super(aClass, bClass);
    }
}
