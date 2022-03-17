package com.custom.wrapper;

import java.util.function.Consumer;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/15 15:43
 * @Desc：查询函数接口
 **/
public interface SelectFunction<Children, T, R> {

    /**
     * 自定义查询字段
     */
    @SuppressWarnings("all")
    Children select(R... columns);
    
    /**
     * sql函数的查询接口
     * 可使用的函数：sum,max,min,count,avg,ifnull
     * @param consumer x -> x.sum(Student::getAge)
     * @return Children
     */
    Children select(Consumer<SqlFunc<T>> consumer);

    /**
     * group by分组
     * @param columns 表字段名称
     * @return Children
     */
    Children groupBy(R... columns);

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

    

}
