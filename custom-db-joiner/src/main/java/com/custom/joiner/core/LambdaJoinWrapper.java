package com.custom.joiner.core;

import com.custom.action.condition.SFunction;
import com.custom.joiner.condition.AbstractJoinConditional;

import java.util.function.Consumer;

/**
 * @author Xiao-Bai
 * @date 2022/8/29 14:59
 * lambda表达式的表连接包装对象
 */
public class LambdaJoinWrapper<T> extends AbstractJoinWrapper<T, LambdaJoinWrapper<T>>
        implements JoinWrapper<T> {


    public LambdaJoinWrapper(Class<T> thisClass) {
        super(thisClass);
    }

    @Override
    public <A, B> LambdaJoinWrapper<T> leftJoin(Class<B> bClass, SFunction<A, ?> aColumn, SFunction<B, ?> bColumn) {
        return this.leftJoin(bClass, join -> join.eq(aColumn, bColumn));
    }

    @Override
    public <A, B> LambdaJoinWrapper<T> leftJoin(AbstractJoinConditional<A, B> joinConditional) {
        return this.addJoinTable(joinConditional);
    }


    @Override
    public <B> LambdaJoinWrapper<T> leftJoin(Class<B> bClass, Consumer<AbstractJoinConditional<A, B>> joinConditional) {
        return this.addJoinTable(bClass, joinConditional);
    }
}
