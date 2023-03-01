package com.custom.tools.tree;

/**
 * @author Xiao-Bai
 * @since 2023/2/19 14:03
 */
public interface ChildrenSeek<T> {


    /**
     * 子节点集查找条件
     */
    boolean seek(T parent, T child);

}
