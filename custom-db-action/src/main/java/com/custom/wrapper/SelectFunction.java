package com.custom.wrapper;

import com.custom.enums.SqlAggregateFunc;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/15 15:43
 * @Desc：查询函数接口
 **/
public interface SelectFunction<Children, R> {

    /**
     * 自定义查询字段
     */
    @SuppressWarnings("all")
    Children select(R... columns);
    
    /**
     * sql函数的查询接口
     */
    Children select(SqlAggregateFunc... funcs);
    
    
    

}
