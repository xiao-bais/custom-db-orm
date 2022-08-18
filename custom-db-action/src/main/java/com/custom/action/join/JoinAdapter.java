package com.custom.action.join;

import com.custom.action.join.condition.ConditionJoiner;
import com.custom.action.join.condition.LambdaConditionJoiner;

/**
 * @Author Xiao-Bai
 * @Date 2022/8/19 2:45
 * @Desc 表关联的中间适配器
 */
public class JoinAdapter<Children, Primary, Join> implements JoinWrapper<Children, Primary, Join> {


    @Override
    public Children alias(String joinAlias) {
        return null;
    }

    @Override
    public Children on(ConditionJoiner<LambdaConditionJoiner<Primary, Join>, Primary, Join> condition) {
        return null;
    }
}
