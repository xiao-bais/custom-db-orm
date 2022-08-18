package com.custom.action.join.condition;

import com.custom.action.condition.SFunction;

/**
 * @Author Xiao-Bai
 * @Date 2022/8/19 2:26
 * @Desc 条件关联
 */
public interface ConditionJoiner<Children, Primary, Join> {

    Children eq(SFunction<Primary, ?> pColumn, SFunction<Join, ?> jColumn);

}
