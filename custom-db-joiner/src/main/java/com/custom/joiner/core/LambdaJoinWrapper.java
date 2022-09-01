package com.custom.joiner.core;

import com.custom.comm.enums.DbJoinStyle;
import com.custom.comm.enums.AliasStrategy;

import java.util.function.Consumer;

/**
 * @author Xiao-Bai
 * @date 2022/8/29 14:59
 * lambda表达式的表连接包装对象
 */
public class LambdaJoinWrapper<T> extends AbstractJoinWrapper<T> implements JoinWrapper<T> {


    public LambdaJoinWrapper(Class<T> thisClass) {
        super(thisClass);
    }

    @Override
    public <R> LambdaJoinWrapper<T> leftJoin(AbstractJoinConditional<R> joinConditional) {
        return addJoinTable(DbJoinStyle.LEFT, joinConditional);
    }

    @Override
    public <R> LambdaJoinWrapper<T> leftJoin(Class<R> joinClass, Consumer<AbstractJoinConditional<R>> joinConditional) {
        return addJoinTable(DbJoinStyle.LEFT, joinClass, joinConditional);
    }

    @Override
    public LambdaJoinWrapper<T> aliasStrategy(AliasStrategy strategy) {
        return setAliasStrategy(strategy);
    }

    @Override
    public <R> LambdaJoinWrapper<T> rightJoin(AbstractJoinConditional<R> joinConditional) {
        return addJoinTable(DbJoinStyle.RIGHT, joinConditional);
    }

    @Override
    public <R> LambdaJoinWrapper<T> rightJoin(Class<R> joinClass, Consumer<AbstractJoinConditional<R>> joinConditional) {
        return addJoinTable(DbJoinStyle.RIGHT, joinClass, joinConditional);
    }

    @Override
    public <R> LambdaJoinWrapper<T> innerJoin(AbstractJoinConditional<R> joinConditional) {
        return addJoinTable(DbJoinStyle.INNER, joinConditional);
    }

    @Override
    public <R> LambdaJoinWrapper<T> innerJoin(Class<R> joinClass, Consumer<AbstractJoinConditional<R>> joinConditional) {
        return addJoinTable(DbJoinStyle.INNER, joinClass, joinConditional);
    }
}
