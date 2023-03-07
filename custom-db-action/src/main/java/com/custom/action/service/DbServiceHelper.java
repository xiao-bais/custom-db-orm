package com.custom.action.service;

import com.custom.action.condition.Conditions;
import com.custom.action.condition.DefaultConditionWrapper;
import com.custom.action.condition.LambdaConditionWrapper;
import com.custom.action.core.DoTargetExecutor;
import com.custom.action.interfaces.TableExecutor;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * service层继承该类，即可拥有增删改查功能
 * @author   Xiao-Bai
 * @since 2023/2/6 22:26
 * @param <T> 实体类映射对象
 */
public interface DbServiceHelper<T> {

    /**
     * 执行的目标类
     */
    Class<T> target();


    /**
     * 执行目标模板
     */
    TableExecutor<T, Serializable> targetTemplate();

    /**
     * 根据主键批量查询
     */
    default List<T> getByKeys(Collection<Serializable> keys) throws Exception {
        return targetTemplate().selectByKeys(keys);
    }

    default Stream<T> getByKeysStream(Collection<Serializable> keys) throws Exception {
        return getByKeys(keys).stream();
    }

    /**
     * 根据主键查询一条记录
     */
    default T getByKey(Serializable key) throws Exception {
        return targetTemplate().selectByKey(key);
    }

    default T getByKey(Serializable key, Supplier<T> supplier) throws Exception {
        return Optional.ofNullable(getByKey(key)).orElseGet(supplier);
    }

    /**
     * 普通查询1
     */
    default DoTargetExecutor<T> where(DefaultConditionWrapper<T> wrapper) {
        return DoTargetExecutor.build(wrapper, targetTemplate());
    }

    /**
     * 普通查询2
     */
    default DoTargetExecutor<T> where(Consumer<DefaultConditionWrapper<T>> consumer) {
        DefaultConditionWrapper<T> wrapper = Conditions.query(target());
        consumer.accept(wrapper);
        return DoTargetExecutor.build(wrapper, targetTemplate());
    }


    /**
     * lambda expression 表达式查询1
     */
    default DoTargetExecutor<T> whereEx(LambdaConditionWrapper<T> wrapper) {
        return DoTargetExecutor.build(wrapper, targetTemplate());
    }

    /**
     * lambda expression 表达式查询2
     */
    default DoTargetExecutor<T> whereEx(Consumer<LambdaConditionWrapper<T>> consumer) {
        LambdaConditionWrapper<T> wrapper = Conditions.lambdaQuery(target());
        consumer.accept(wrapper);
        return DoTargetExecutor.build(wrapper, targetTemplate());
    }

    /**
     * 插入
     */
    default boolean insert(T entity) throws Exception {
        return targetTemplate().insert(entity) > 0;
    }

    /**
     * 批量插入
     */
    default boolean insertBatch(List<T> list) throws Exception {
        return targetTemplate().insert(list) > 0;
    }

    /**
     * 根据主键修改不为null的字段
     */
    default boolean update(T entity) throws Exception {
        return targetTemplate().updateByKey(entity) > 0;
    }

    /**
     * 存在主键则修改，不存在则插入
     */
    default boolean save(T entity) throws Exception {
        return targetTemplate().save(entity) > 0;
    }

    /**
     * 根据主键删除
     */
    default boolean deleteByKey(Serializable key) throws Exception {
        return targetTemplate().deleteByKey(key) > 0;
    }

    /**
     * 根据主键删除
     */
    default boolean deleteBatchKeys(Collection<Serializable> keys) throws Exception {
        return targetTemplate().deleteBatchKeys(keys) > 0;
    }
}
