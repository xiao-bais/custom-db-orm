package com.custom.joiner.condition;

import com.custom.action.condition.SFunction;

import java.util.Collection;

/**
 * @author Xiao-Bai
 * @date 2022/8/29 14:11
 * @desc
 */
public class LambdaJoinConditional<T, A> extends AbstractJoinConditional<T, A> {
    @Override
    public LambdaJoinConditional<T, A> alias(String joinAlias) {
        return null;
    }

    @Override
    public LambdaJoinConditional<T, A> eq(SFunction<A, ?> aColumn, SFunction<T, ?> bColumn) {
        return applyCondition(() -> this.formatJoinCondition(this.toAColumn(aColumn), this.toBColumn(bColumn)));
    }

    @Override
    public LambdaJoinConditional<T, A> eq(SFunction<T, ?> bColumn, Object val) {
        return null;
    }

    @Override
    public LambdaJoinConditional<T, A> gt(SFunction<T, ?> bColumn, Object val) {
        return null;
    }

    @Override
    public LambdaJoinConditional<T, A> ge(SFunction<T, ?> bColumn, Object val) {
        return null;
    }

    @Override
    public LambdaJoinConditional<T, A> lt(SFunction<T, ?> bColumn, Object val) {
        return null;
    }

    @Override
    public LambdaJoinConditional<T, A> le(SFunction<T, ?> bColumn, Object val) {
        return null;
    }

    @Override
    public LambdaJoinConditional<T, A> between(SFunction<T, ?> bColumn, Object val1, Object val2) {
        return null;
    }

    @Override
    public LambdaJoinConditional<T, A> in(SFunction<T, ?> bColumn, Object... values) {
        return null;
    }

    @Override
    public LambdaJoinConditional<T, A> in(SFunction<T, ?> bColumn, Collection<?> val) {
        return null;
    }

    public LambdaJoinConditional(Class<T> bClass) {
        super(bClass);
    }
}
