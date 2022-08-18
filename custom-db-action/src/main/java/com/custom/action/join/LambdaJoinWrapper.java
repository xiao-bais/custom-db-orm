package com.custom.action.join;

import com.custom.action.join.condition.ConditionJoiner;
import com.custom.action.join.condition.LambdaConditionJoiner;

/**
 * @Author Xiao-Bai
 * @Date 2022/8/19 2:37
 * @Desc
 */
public class LambdaJoinWrapper<Primary, Join> implements JoinWrapper<LambdaJoinWrapper<Primary, Join>, Primary, Join> {


    @Override
    public LambdaJoinWrapper<Primary, Join> join(Class<Join> joinTable) {
        return null;
    }

    @Override
    public LambdaJoinWrapper<Primary, Join> alias(String joinAlias) {
        return null;
    }

    @Override
    public LambdaJoinWrapper<Primary, Join> on(ConditionJoiner<LambdaConditionJoiner<Primary, Join>, Primary, Join> condition) {
        return null;
    }
}
