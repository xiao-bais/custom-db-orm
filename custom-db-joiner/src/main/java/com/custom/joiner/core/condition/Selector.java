package com.custom.joiner.core.condition;

import com.custom.comm.utils.lambda.SFunction;
import com.custom.action.condition.SelectFunc;

import java.util.function.Consumer;

/**
 * @Author Xiao-Bai
 * @Date 2022/9/1 0001 10:15
 * @Desc
 */
@SuppressWarnings("all")
public interface Selector<Children> {

    /**
     * 自定义查询字段
     */
    <R> Children select(SFunction<R, ?>... columns);

    /**
     * sql函数的查询接口
     * 可使用的函数：sum,max,min,count,avg,ifnull
     * @param consumer x -> x.sum(Student::getAge)
     * @return Children
     */
    <R> Children select(Consumer<SelectFunc<R>> consumer);
    <R> Children select(SelectFunc<R> selectFunc);

    /**
     * group by分组
     * @param columns 表字段名称
     * @return Children
     */
//    <R> Children groupBy(SFunction<R, ?>... columns);

    /**
     * having函数
     * @param havingSql havingSql
     * @param params 参数值 ? -> value
     * @return Children
     */
//    Children having(boolean condition, String havingSql, Object... params);
//    default Children having(String havingSql, Object... params) {
//        return having(true, havingSql, params);
//    }

    /**
     *  limit分页
     * @param condition 是否满足条件
     * @param pageIndex 第几页
     * @param pageSize 每页的记录数量
     * @return
     */
//    Children pageParams(boolean condition, Integer pageIndex, Integer pageSize);
//    default Children pageParams(Integer pageIndex, Integer pageSize) {
//        return pageParams(true, pageIndex, pageSize);
//    }


    /**
     * order by排序（升序）
     * @param condition 是否满足排序的条件
     * @param consumer 消费型排序函数
     * @return Children
     */
//    <R> Children orderByAsc(boolean condition, Consumer<OrderByFunc<R>> consumer);
//    default <R> Children orderByAsc(Consumer<OrderByFunc<R>> consumer) {
//        return orderByAsc(true, consumer);
//    }
//
//    <R> Children orderByAsc(boolean condition, OrderByFunc<R> orderByFunc);
//    default <R> Children orderByAsc(OrderByFunc<R> orderByFunc) {
//        return orderByAsc(true, orderByFunc);
//    }

    /**
     * order by排序（降序）
     * @param condition 是否满足排序的条件
     * @param consumer 消费型排序函数
     * @return Children
     */
//    <R> Children orderByDesc(boolean condition, Consumer<OrderByFunc<R>> consumer);
//    default <R> Children orderByDesc(Consumer<OrderByFunc<R>> consumer) {
//        return orderByDesc(true, consumer);
//    }
//
//    <R> Children orderByDesc(boolean condition, OrderByFunc<R> orderByFunc);
//    default <R> Children orderByDesc(OrderByFunc<R> orderByFunc) {
//        return orderByDesc(true, orderByFunc);
//    }
//
//
//    <R> Children orderByAsc(boolean condition, SFunction<R, ?>... columns);
//    default <R> Children orderByAsc(SFunction<R, ?>... columns) {
//        return orderByAsc(true, columns);
//    }
//
//    <R> Children orderByDesc(boolean condition, SFunction<R, ?>... columns);
//    default <R> Children orderByDesc(SFunction<R, ?>... columns) {
//        return orderByDesc(true, columns);
//    }
}
