package com.custom.tools.tree;

import java.util.List;

/**
 * 设置子集的接口
 * @author Xiao-Bai
 * @since 2023/2/19 14:39
 */
public interface ChildrenSet<T> {

    void accept(T t, List<T> children);

}
