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
    public <R, A> LambdaJoinWrapper<T> leftJoin(AbstractJoinConditional<R> joinConditional) {
        return addJoinTable(joinConditional);
    }

    @Override
    public <R, A> LambdaJoinWrapper<T> leftJoin(Class<R> joinClass, Consumer<AbstractJoinConditional<R>> joinConditional) {
        return addJoinTable(joinClass, joinConditional);
    }
}
