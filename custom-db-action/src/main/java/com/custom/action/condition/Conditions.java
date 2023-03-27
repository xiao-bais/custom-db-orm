package com.custom.action.condition;

import com.custom.action.core.syncquery.SyncQueryWrapper;

/**
 * 静态条件构造工具类
 * @author   Xiao-Bai
 * @since  2022/3/15 14:41
 **/
@SuppressWarnings("unchecked")
public class Conditions {

    private Conditions() {
    }

    /**
     * 以lambda表达式的方式去进行sql的条件构造
     */
    public static <T> LambdaConditionWrapper<T> lambdaQuery(Class<T> entityClass) {
        return new LambdaConditionWrapper<>(entityClass);
    }

    /**
     * 默认以键值对的方式去进行sql的条件构造
     */
    public static <T> DefaultConditionWrapper<T> query(Class<T> entityClass) {
        return new DefaultConditionWrapper<>(entityClass);
    }

    /**
     * 构造一个空的条件构造器，适合用于没有查询条件的查询
     * 仅可使用分页(pageParams)、排序(orderBy)、分组(groupBy)、分组后过滤(having)以及查询字段的筛选(select)
     */
    public static <T> EmptyConditionWrapper<T> emptyQuery(Class<T> entityClass) {
        return new EmptyConditionWrapper<>(entityClass);
    }

    /**
     * 给定一个条件为全等的条件构造器
     * 参数中对象的所有属性条件皆为等于（只会拼接属性值!=null的条件）
     * @param entity 全等条件的任意实体bean对象
     * @param <T>
     * @return
     */
    public static <T> DefaultConditionWrapper<T> allEqQuery(T entity) {
        DefaultConditionWrapper<T> conditionWrapper = new DefaultConditionWrapper<>((Class<T>) entity.getClass());
        AllEqualConditionHandler<T> equalConditionHandler = new AllEqualConditionHandler<>(entity, conditionWrapper);
        equalConditionHandler.allExecEqCondition();
        return conditionWrapper;
    }

    /**
     * 给定一个默认的update sql set设置器 + 默认条件构造器
     * @param entityClass
     * @param <T>
     * @return
     */
    public static <T> DefaultUpdateSet<T> update(Class<T> entityClass) {
        return new DefaultUpdateSet<>(entityClass);
    }

    /**
     * 给定一个lambda表达式的update sql set设置器 + lambda的条件构造器
     * @param entityClass
     * @param <T>
     * @return
     */
    public static <T> LambdaUpdateSet<T> lambdaUpdate(Class<T> entityClass) {
        return new LambdaUpdateSet<>(entityClass);
    }

    /**
     * 给定一个同步查询的包装器
     * @param entityClass
     * @param <T>
     * @return
     */
    public static <T> SyncQueryWrapper<T> syncQuery(Class<T> entityClass) {
        return new SyncQueryWrapper<>(entityClass);
    }
}
