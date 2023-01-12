package com.custom.action.condition;

import com.custom.comm.utils.lambda.SFunction;

/**
 * @author Xiao-Bai
 * @date 2023/1/12 0012 10:58
 */
public interface ExistsWrapper<P, E> {

   /**
    * exists中关联条件(a.column = existsTable.column)
    * @param proColumn 主表字段
    * @param existColumn exists对应的字段
    */
   LambdaConditionWrapper<E> apply(SFunction<P, ?> proColumn, SFunction<E, ?> existColumn);

}
