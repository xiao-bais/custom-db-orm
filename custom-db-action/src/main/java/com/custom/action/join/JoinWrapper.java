package com.custom.action.join;

import com.custom.action.join.condition.ConditionJoiner;
import com.custom.action.join.condition.LambdaConditionJoiner;

/**
 * @Author Xiao-Bai
 * @Date 2022/8/19 1:56
 * @Desc
 */
public interface JoinWrapper<Children, Primary, Join> {

    /**
     * 关联表别名
     */
    Children alias(String joinAlias);

    /**
     * 关联条件
     */
    Children on(ConditionJoiner<LambdaConditionJoiner<Primary, Join>, Primary, Join> condition);




}
