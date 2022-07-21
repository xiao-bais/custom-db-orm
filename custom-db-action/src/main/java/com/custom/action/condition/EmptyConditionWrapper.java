package com.custom.action.condition;

import java.util.function.Consumer;

/**
 * @Author Xiao-Bai
 * @Date 2022/5/19 14:31
 * @Desc：一个空的构造器。
 * 说明：主要用于查询全部，适合用于没有查询条件的查询
 * 如: 分页(pageParams)、排序(orderBy)、分组(groupBy)、分组后过滤(having)以及查询字段的筛选(select)
 **/
public class EmptyConditionWrapper<T> extends ConditionAdapter<T, EmptyConditionWrapper<T>>{
    @Override
    public T getThisEntity() {
        throw new UnsupportedOperationException();
    }

    public EmptyConditionWrapper(Class<T> entityClass) {
        wrapperInitialize(entityClass);
    }

    @Override
    protected EmptyConditionWrapper<T> getInstance() {
        return new EmptyConditionWrapper<>(getEntityClass());
    }

    @Override
    public EmptyConditionWrapper<T> or(boolean condition, EmptyConditionWrapper<T> wrapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EmptyConditionWrapper<T> or(boolean condition, Consumer<EmptyConditionWrapper<T>> consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EmptyConditionWrapper<T> or(boolean condition) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EmptyConditionWrapper<T> and(boolean condition, EmptyConditionWrapper<T> wrapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EmptyConditionWrapper<T> and(boolean condition, Consumer<EmptyConditionWrapper<T>> consumer) {
        throw new UnsupportedOperationException();
    }
}
