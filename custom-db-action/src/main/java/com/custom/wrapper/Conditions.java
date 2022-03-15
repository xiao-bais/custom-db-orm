package com.custom.wrapper;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/15 14:41
 * @Desc：
 **/
public class Conditions {

    /**
     * 以lambda表达式的方式去进行sql的条件构造
     */
    public static <T> LambdaConditionEntity<T> lambdaConditionQuery(Class<T> entityClass) {
        return new LambdaConditionEntity<>(entityClass);
    }

    /**
     * 以键值对的方式去进行sql的条件构造
     */
    public static <T> ConditionEntity<T> conditionQuery(Class<T> entityClass) {
        return new ConditionEntity<>(entityClass);
    }
    
}
