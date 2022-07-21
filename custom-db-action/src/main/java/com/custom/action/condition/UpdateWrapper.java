package com.custom.action.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/27 0027 11:59
 * @Desc
 */
public class UpdateWrapper<T> {

    /**
     * 修改的实体
     */
    private T entity;

    /**
     * set的sql
     */
    private List<Supplier<String>> setSqlList = new ArrayList<>();

    /**
     * set的value
     */
    private List<Object> setParams = new ArrayList<>();

    /**
     * 修改的条件参数值
     */
    private List<Object> sqlParams = new ArrayList<>();

    /**
     * 修改的sql条件
     */
    private StringBuilder finalCondition = new StringBuilder();




}
