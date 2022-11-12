package com.custom.jdbc.handler;

/**
 * @author Xiao-Bai
 * @date 2022/11/13 1:15
 * @desc
 */
public interface NonNullableTypeHandler<T> {


    /**
     * 获取本类型的值，并且不为空
     */
    T getTypeNoNullValue(Object val);
}
