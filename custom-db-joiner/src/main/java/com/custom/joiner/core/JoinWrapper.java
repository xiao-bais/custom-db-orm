package com.custom.joiner.core;

import com.custom.comm.utils.lambda.SFunction;
import com.custom.comm.enums.AliasStrategy;

import java.util.function.Consumer;

/**
 * @author Xiao-Bai
 * <li>基础条件查询构造器，单表，多表关联查询皆可</li>
 * <li>T - 主表对应的实体类</li>
 * <li>A - 主表或者说是已关联的表对应的实体类</li>
 * <li>R - 本次需关联的表对应的实体类</li>
 * <br/> 例如：
 * select
 * from student stu
 * left join city cy on cy.id = stu.city_id
 * <br/> 那么 student便是A，而city便是R
 */
@SuppressWarnings("all")
public interface JoinWrapper<T> {

    /**
     * 表关联[left join]
     * @param aColumn 主关联字段
     * @param bColumn 副关联字段
     *                <p>
     *                 left join R r on r.rColumn = a.aColumn
     *                </p>
     * @param <A> 主表对应实体类
     * @param <R> 关联表对应实体类
     * @return Result
     */
    default <R, A> LambdaJoinWrapper<T> leftJoin(Class<R> joinClass, SFunction<R, ?> joinColumn, SFunction<A, ?> aColumn) {
        return leftJoin(joinClass, join -> join.eq(joinColumn, aColumn));
    }
    <R> LambdaJoinWrapper<T> leftJoin(AbstractJoinConditional<R> joinConditional);
    <R> LambdaJoinWrapper<T> leftJoin(Class<R> joinClass, Consumer<AbstractJoinConditional<R>> joinConditional);

    /**
     * 别名策略
     * 不填默认规则为 {@link AliasStrategy#FIRST_APPEND}
     */
    LambdaJoinWrapper<T> aliasStrategy(AliasStrategy strategy);


    default <R, A> LambdaJoinWrapper<T> rightJoin(Class<R> joinClass, SFunction<R, ?> joinColumn, SFunction<A, ?> aColumn) {
        return rightJoin(joinClass, join -> join.eq(joinColumn, aColumn));
    }
    <R> LambdaJoinWrapper<T> rightJoin(AbstractJoinConditional<R> joinConditional);
    <R> LambdaJoinWrapper<T> rightJoin(Class<R> joinClass, Consumer<AbstractJoinConditional<R>> joinConditional);

    default <R, A> LambdaJoinWrapper<T> innerJoin(Class<R> joinClass, SFunction<R, ?> joinColumn, SFunction<A, ?> aColumn) {
        return innerJoin(joinClass, join -> join.eq(joinColumn, aColumn));
    }
    <R> LambdaJoinWrapper<T> innerJoin(AbstractJoinConditional<R> joinConditional);
    <R> LambdaJoinWrapper<T> innerJoin(Class<R> joinClass, Consumer<AbstractJoinConditional<R>> joinConditional);










}
