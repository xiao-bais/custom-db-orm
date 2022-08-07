package com.custom.action.condition;

import java.util.function.Consumer;

/**
 * @Author Xiao-Bai
 * @Date 2022/8/6 16:28
 * @Desc sql set修改器
 * Children - 子类实例类型
 * Setter - sql set 设置器类型
 * Wrapper - 条件构造器类型
 */
public interface UpdateSet<Children, Setter, Wrapper> {

    
    /**
     * sql set设置器
     */
    Children setter(boolean condition, Setter setter);

    default Children setter(Setter setter) {
        return setter(true, setter);
    }

    /**
     * sql set设置器（消费型）
     */
    Children setter(boolean condition, Consumer<Setter> consumer);

    default Children setter(Consumer<Setter> consumer) {
        return setter(true, consumer);
    }

    /**
     * sql 条件构造器
     */
    Children where(boolean condition, Wrapper wrapper);

    default  Children where(Wrapper wrapper) {
        return where(true, wrapper);
    }

    Children where(boolean condition, Consumer<Wrapper> consumer);

    default  Children where(Consumer<Wrapper> consumer) {
        return where(true, consumer);
    }

}
