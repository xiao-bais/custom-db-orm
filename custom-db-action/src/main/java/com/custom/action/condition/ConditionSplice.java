package com.custom.action.condition;

import java.util.function.Consumer;

/**
 * @author Xiao-Bai
 * @date 2022/3/13 23:43
 * @desc:大条件拼接组装
 */
public interface ConditionSplice<Children> {

    /**
     * 拼接sql的or条件
     * 例如：where a.age = 30 or (a.name = 'zhangsan')
     * @param wrapper 条件构造对象
     * @return Children
     */
    Children or(boolean condition, Children wrapper);
    default Children or(Children wrapper) {
       return or(true, wrapper);
    }

    /**
     * 消费型
     * @param condition 是否满足条件
     * @param consumer 条件构造对象
     * @return Children
     */
    Children or(boolean condition, Consumer<Children> consumer);
    default Children or(Consumer<Children> consumer) {
        return or(true, consumer);
    }


    /**
     * sql or条件
     * 若condition为false，则截止到再次调用该类任何方法前，该方法的后面一系列所有的调用条件串，都不会成立
     * 例：若condition = false
     * 则1：or(false).eq(false).gt(false).....
     * 则2：or(false).eq(false).gt(false).or(new().eq(true/false)).ge(true).....
     * @param condition 是否满足条件
     * @return Children
     */
    Children or(boolean condition);
    default Children or() {
        return or(true);
    }

    /**
     * 拼接sql的and条件
     * 例如：where a.age = 30 and (a.name = 'zhangsan')
     * @param wrapper 条件构造对象
     * @return children
     */
    Children and(boolean condition, Children wrapper);
    default Children and(Children wrapper) {
        return and(true, wrapper);
    }

    /**
     * 消费型
     * @param condition 是否满足条件
     * @param consumer 消费型条件构造对象
     * @return children
     */
    Children and(boolean condition, Consumer<Children> consumer);
    default Children and(Consumer<Children> consumer) {
        return and(true, consumer);
    }


}
