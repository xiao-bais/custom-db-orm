package com.custom.joiner.core;

import com.custom.action.condition.SFunction;
import com.custom.joiner.condition.AbstractJoinConditional;

import java.util.function.Consumer;

/**
 * @author Xiao-Bai
 * <li>基础条件查询构造器，单表，多表关联查询皆可</li>
 * <li>T - 主表对应的实体类</li>
 * <li>A - 主表或者说是已关联的表对应的实体类</li>
 * <li>B - 本次关联的表对应的实体类</li>
 * <br/> 例如：
 * select
 * from student stu
 * left join city cy on cy.id = stu.city_id
 * <br/> 那么 student便是A，而city便是B
 */
@SuppressWarnings("all")
public interface JoinWrapper<T> {

    /**
     * 表关联[left join]
     * @param aColumn 主关联字段
     * @param bColumn 副关联字段
     *                <p>
     *                 left join B b on a.aColumn = b.bColumn
     *                </p>
     * @param <A> 主表对应实体类
     * @param <R> 关联表对应实体类
     * @return Result
     */
    default <R, A> LambdaJoinWrapper<T> leftJoin(Class<R> joinClass, SFunction<R, ?> joinColumn, SFunction<A, ?> aColumn) {
        return leftJoin(joinClass, join -> join.eq(joinColumn, aColumn));
    }
    <R, A> LambdaJoinWrapper<T> leftJoin(AbstractJoinConditional<R, A> joinConditional);
    <R, A> LambdaJoinWrapper<T> leftJoin(Class<R> joinClass, Consumer<AbstractJoinConditional<R, A>> joinConditional);






}
