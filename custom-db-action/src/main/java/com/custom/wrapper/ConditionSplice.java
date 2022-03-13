package com.custom.wrapper;

import com.custom.enums.DbSymbol;

/**
 * @author Xiao-Bai
 * @date 2022/3/13 23:43
 * @desc:大条件拼接组装
 */
public interface ConditionSplice<Children, R> {

    /**
     * 拼接sql的or条件
     * 例如：where a.age = 30 or (a.name = 'zhangsan')
     * @param wrapper
     * @return children
     */
    Children or(boolean condition, Children wrapper);
    default Children or(Children wrapper) {
       return or(true, wrapper);
    }

    /**
     * 拼接sql的and条件
     * 例如：where a.age = 30 and (a.name = 'zhangsan')
     * @param wrapper
     * @return children
     */
    Children and(boolean condition, Children wrapper);
    default Children and(Children wrapper) {
        return or(true, wrapper);
    }

    /**
     * 自定义查询字段
     */
    @SuppressWarnings("all")
    public abstract Children select(R... columns);


}
