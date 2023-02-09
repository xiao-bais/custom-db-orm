package com.custom.action.condition;

import java.util.function.Consumer;

/**
 * 查询函数接口
 * @author   Xiao-Bai
 * @since  2022/3/15 15:43
 **/
@SuppressWarnings("all")
public interface QueryFunction<Children, T, Param> {

    /**
     * 自定义查询字段
     */
    @SuppressWarnings("all")
    Children select(Param... columns);
    
    /**
     * sql函数的查询接口
     * 可使用的函数：sum,max,min,count,avg,ifnull
     * @param consumer x -> x.sum(Student::getAge)
     * @return Children
     */
    Children select(Consumer<SelectFunc<T>> consumer);
    Children select(SelectFunc<T> selectFunc);

    /**
     * group by分组
     * @param columns 表字段名称
     * @return Children
     */
    Children groupBy(Param... columns);

    /**
     * having函数
     * @param havingSql havingSql
     * @param params 参数值 ? -> value
     * @return Children
     */
    Children having(boolean condition, String havingSql, Object... params);
    default Children having(String havingSql, Object... params) {
        return having(true, havingSql, params);
    }

    /**
     *  limit分页
     * @param condition 是否满足条件
     * @param pageIndex 第几页
     * @param pageSize 每页的记录数量
     * @return
     */
    Children pageParams(boolean condition, Integer pageIndex, Integer pageSize);
    default Children pageParams(Integer pageIndex, Integer pageSize) {
        return pageParams(true, pageIndex, pageSize);
    }


    /**
     * order by排序（升序）
     * @param condition 是否满足排序的条件
     * @param consumer 消费型排序函数
     * @return Children
     */
    Children orderByAsc(boolean condition, Consumer<OrderByFunc<T>> consumer);
    default Children orderByAsc(Consumer<OrderByFunc<T>> consumer) {
        return orderByAsc(true, consumer);
    }

    Children orderByAsc(boolean condition, OrderByFunc<T> orderByFunc);
    default Children orderByAsc(OrderByFunc<T> orderByFunc) {
        return orderByAsc(true, orderByFunc);
    }

    /**
     * order by排序（降序）
     * @param condition 是否满足排序的条件
     * @param consumer 消费型排序函数
     * @return Children
     */
    Children orderByDesc(boolean condition, Consumer<OrderByFunc<T>> consumer);
    default Children orderByDesc(Consumer<OrderByFunc<T>> consumer) {
        return orderByDesc(true, consumer);
    }

    Children orderByDesc(boolean condition, OrderByFunc<T> orderByFunc);
    default Children orderByDesc(OrderByFunc<T> orderByFunc) {
        return orderByDesc(true, orderByFunc);
    }



    Children orderByAsc(boolean condition, Param... columns);
    default Children orderByAsc(Param... columns) {
        return orderByAsc(true, columns);
    }

    Children orderByDesc(boolean condition, Param... columns);
    default Children orderByDesc(Param... columns) {
        return orderByDesc(true, columns);
    }



    Children orderByAsc(boolean condition, Param column);
    default Children orderByAsc(Param column) {
        return orderByAsc(true, column);
    }

    Children orderByDesc(boolean condition, Param column);
    default Children orderByDesc(Param column) {
        return orderByDesc(true, column);
    }

}
