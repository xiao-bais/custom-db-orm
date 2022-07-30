package com.custom.action.condition;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/15 14:41
 * @Desc：静态条件构造器
 **/
public class Conditions {

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
     * @param entityClass 查询的实体Class对象
     * @param entity 全等条件的任意实体bean对象
     * @param <T>
     * @return
     */
    public static <T> DefaultConditionWrapper<T> allEqQuery(Class<T> entityClass, Object entity) {
        DefaultConditionWrapper<T> conditionWrapper = new DefaultConditionWrapper<>(entityClass);
        AllEqualConditionHandler<T> equalConditionHandler = new AllEqualConditionHandler<>(entity,
                conditionWrapper.getTableSqlBuilder().getFieldMapper(), conditionWrapper);
        equalConditionHandler.allEqCondition();
        return conditionWrapper;
    }
    
}
