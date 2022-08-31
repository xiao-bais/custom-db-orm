package com.custom.joiner.core;

import com.custom.comm.enums.DbJoinStyle;

import java.util.function.Consumer;

/**
 * @author Xiao-Bai
 * @date 2022/8/29 14:59
 * lambda表达式的表连接包装对象
 */
public class LambdaJoinStyleWrapper<T> extends AbstractJoinStyleWrapper<T, LambdaJoinStyleWrapper<T>>
        implements JoinStyleWrapper<T> {


    public LambdaJoinStyleWrapper(Class<T> thisClass) {
        super(thisClass);
    }

    @Override
    public <R> LambdaJoinStyleWrapper<T> leftJoin(AbstractJoinConditional<R> joinConditional) {
        return addJoinTable(DbJoinStyle.LEFT, joinConditional);
    }

    @Override
    public <R> LambdaJoinStyleWrapper<T> leftJoin(Class<R> joinClass, Consumer<AbstractJoinConditional<R>> joinConditional) {
        return addJoinTable(DbJoinStyle.LEFT, joinClass, joinConditional);
    }
}
