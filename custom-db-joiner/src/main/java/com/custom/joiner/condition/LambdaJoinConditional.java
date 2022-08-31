package com.custom.joiner.condition;

import com.custom.action.condition.SFunction;

import java.util.Collection;

/**
 * @author Xiao-Bai
 * @date 2022/8/29 14:11
 * @desc
 */
public class LambdaJoinConditional<T> extends AbstractJoinConditional<T> {

    @Override
    public <A> LambdaJoinConditional<T> eq(SFunction<T, ?> joinColumn, SFunction<A, ?> aColumn) {
        this.resloveColumn(joinColumn, aColumn);
        this.isReslove = true;
        return childrenThis;
    }

    @Override
    public LambdaJoinConditional<T> eq(SFunction<T, ?> joinColumn, Object val) {
        this.thisConditionWrapper().eq(joinColumn, val);
        return childrenThis;
    }

    @Override
    public LambdaJoinConditional<T> gt(SFunction<T, ?> joinColumn, Object val) {
        this.thisConditionWrapper().gt(joinColumn, val);
        return childrenThis;
    }

    @Override
    public LambdaJoinConditional<T> ge(SFunction<T, ?> joinColumn, Object val) {
        this.thisConditionWrapper().ge(joinColumn, val);
        return childrenThis;
    }

    @Override
    public LambdaJoinConditional<T> lt(SFunction<T, ?> joinColumn, Object val) {
        this.thisConditionWrapper().lt(joinColumn, val);
        return childrenThis;
    }

    @Override
    public LambdaJoinConditional<T> le(SFunction<T, ?> joinColumn, Object val) {
        this.thisConditionWrapper().le(joinColumn, val);
        return childrenThis;
    }

    @Override
    public LambdaJoinConditional<T> between(SFunction<T, ?> joinColumn, Object val1, Object val2) {
        this.thisConditionWrapper().between(joinColumn, val1, val2);
        return childrenThis;
    }

    @Override
    public LambdaJoinConditional<T> in(SFunction<T, ?> joinColumn, Object... values) {
        this.thisConditionWrapper().in(joinColumn, values);
        return childrenThis;
    }

    @Override
    public LambdaJoinConditional<T> in(SFunction<T, ?> joinColumn, Collection<?> val) {
        this.thisConditionWrapper().eq(joinColumn, val);
        return childrenThis;
    }


    public LambdaJoinConditional(Class<T> bClass) {
        super(bClass);
    }
}
