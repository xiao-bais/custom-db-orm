package com.custom.jdbc.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Xiao-Bai
 * @date 2022/11/12 0:11
 * @desc 类型转换处理
 */
public interface TypeHandler<T> {

    /**
     * 获取本类型的值
     */
    T getTypeValue(Object val);


    /**
     * 获取结果集中的值
     * @param rs
     * @param column 列名
     * @return
     */
    T getTypeValue(ResultSet rs, String column) throws SQLException;


    /**
     * 获取结果集中的值
     * @param rs
     * @param index 第几列
     * @return
     */
    T getTypeValue(ResultSet rs, int index) throws SQLException;


    /**
     * 获取本类型的值，并且不为空
     */
    T getTypeNoNullValue(Object val);





}
