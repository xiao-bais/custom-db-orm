package com.custom.tools.data;

import java.util.function.Predicate;

/**
 * @author Xiao-Bai
 * @date 2022/11/23 0:45
 * @desc
 */
public interface JoinCondition<T> {

    /**
     * 关联的条件
     */
    Predicate<T> doJoin(T obj);

}
