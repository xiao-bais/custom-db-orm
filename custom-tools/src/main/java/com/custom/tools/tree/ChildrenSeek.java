package com.custom.tools.tree;

import java.util.function.Predicate;

/**
 * @author Xiao-Bai
 * @since 2023/2/19 14:03
 */
public interface ChildrenSeek<T> {


    /**
     * 子节点集查找
     * @param parent 父节点
     */
    Predicate<T> seek(T parent);

}
