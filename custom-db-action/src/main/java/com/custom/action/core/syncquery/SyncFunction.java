package com.custom.action.core.syncquery;

import com.custom.action.condition.ConditionWrapper;

/**
 * @author Xiao-Bai
 * @since 2023/3/27 18:22
 */
public interface SyncFunction<T, Other> {

    /**
     * 利用查询好的结果去做其他属性的查询
     * @param res
     */
    ConditionWrapper<Other> doQuery(T res);

}
