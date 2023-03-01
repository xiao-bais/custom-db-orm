package com.custom.tools.data;

/**
 * @author Xiao-Bai
 * @since 2023/3/1 22:44
 */
public interface Joining<T> {

    /**
     * 关联的条件
     * <br/> o1 - 主数据，o2 - 待合并数据
     */
    boolean apply(T o1, T o2);
}
