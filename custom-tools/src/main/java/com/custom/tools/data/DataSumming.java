package com.custom.tools.data;

import java.util.List;

/**
 * @author Xiao-Bai
 * @date 2022/11/25 0025 16:31
 */
@SuppressWarnings("unchecked")
public class DataSumming<T> {

    /**
     * 主数据
     */
    private final T total;

    /**
     * 待求和
     */
    private final List<T> waitSumList;

    /**
     * 当前求和的class
     */
    private Class<T> targetClass;

    public DataSumming(T total, List<T> waitSumList) {
        this.total = total;
        this.waitSumList = waitSumList;
    }

    public void start(String... ignoreFields) {
        if (total == null || waitSumList == null) {
            return;
        }
        targetClass = (Class<T>) total.getClass();

        
        
    }

}
