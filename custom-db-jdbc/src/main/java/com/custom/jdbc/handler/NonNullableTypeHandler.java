package com.custom.jdbc.handler;

/**
 * @author  Xiao-Bai
 * @since  2022/11/13 1:15
 * 
 */
public interface NonNullableTypeHandler<T> {


    /**
     * 获取本类型的值，并且不为空
     */
    T getTypeNoNullValue(Object val);
}
