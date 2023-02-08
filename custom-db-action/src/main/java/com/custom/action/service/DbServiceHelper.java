package com.custom.action.service;

import com.custom.action.activerecord.ActiveModel;
import com.custom.action.condition.*;
import com.custom.action.core.TableInfoCache;
import com.custom.action.interfaces.TableExecutor;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.ReflectUtil;
import com.custom.jdbc.configuration.DbDataSource;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Xiao-Bai
 * @date 2023/2/6 22:26
 */
@SuppressWarnings("unchecked")
public abstract class DbServiceHelper<T> {

    private static final Map<String, Class<?>> CURRENT_TARGET_CACHE = new ConcurrentHashMap<>();


    /**
     * @see DbDataSource#getOrder()
     * 多个数据源的情况下，可由此指定数据源
     * @return {@link DbDataSource#getOrder()}
     */
    protected int order() {
        return Constants.DEFAULT_ONE;
    }

    /**
     * 执行的目标类
     */
    private Class<T> target() {
       return Optional.ofNullable((Class<T>) CURRENT_TARGET_CACHE.get(getClass().toString())).orElseGet(() -> {
            Class<T> thisTarget = ReflectUtil.getThisGenericType(getClass());
            CURRENT_TARGET_CACHE.putIfAbsent(getClass().toString(), thisTarget);
            return thisTarget;
        });
    }

    /**
     * 执行器
     */
    private TableExecutor<T, Serializable> actuator() {
        return TableInfoCache.getTableExecutor(order(), target());
    }

    /**
     * 根据主键批量查询
     */
    public List<T> getByKeys(Collection<Serializable> keys) throws Exception {
        return actuator().selectByKeys(keys);
    }

    public Stream<T> getByKeysStream(Collection<Serializable> keys) throws Exception {
        return getByKeys(keys).stream();
    }

    /**
     * 根据主键查询一条记录
     */
    public T getByKey(Serializable key) throws Exception {
        return actuator().selectByKey(key);
    }

    public T getByKeyOpt(Serializable key, Supplier<T> supplier) throws Exception {
        return Optional.ofNullable(getByKey(key)).orElseGet(supplier);
    }

    /**
     * 普通查询1
     */
    public DbQueryWrapper<T> get(DefaultConditionWrapper<T> wrapper) {
        return DbQueryWrapper.build(wrapper, actuator());
    }

    /**
     * 普通查询2
     */
    public DbQueryWrapper<T> get(Consumer<DefaultConditionWrapper<T>> consumer) {
        DefaultConditionWrapper<T> wrapper = Conditions.query(target());
        consumer.accept(wrapper);
        return DbQueryWrapper.build(wrapper, actuator());
    }


    /**
     * lambda表达式查询1
     */
    public DbQueryWrapper<T> getLambda(LambdaConditionWrapper<T> wrapper) {
        return DbQueryWrapper.build(wrapper, actuator());
    }

    /**
     * lambda表达式查询2
     */
    public DbQueryWrapper<T> getLambda(Consumer<LambdaConditionWrapper<T>> consumer) {
        LambdaConditionWrapper<T> wrapper = Conditions.lambdaQuery(target());
        consumer.accept(wrapper);
        return DbQueryWrapper.build(wrapper, actuator());
    }

    /**
     * 插入
     */
    public boolean insert(T entity) throws Exception {
        return actuator().insert(entity) > 0;
    }

    /**
     * 批量插入
     */
    public boolean insertBatch(List<T> list) throws Exception {
        return actuator().insert(list) > 0;
    }

    /**
     * 根据键修改不为null的字段
     */
    public boolean update(T entity) throws Exception {
        return actuator().updateByKey(entity) > 0;
    }

    /**
     * sql set 设置器
     */
    public boolean update(AbstractUpdateSet<T> updateSet) throws Exception {
        return actuator().updateSelective(updateSet) > 0;
    }

    /**
     * 存在主键则修改，不存在则插入
     */
    public boolean save(T entity) throws Exception {
        return actuator().save(entity) > 0;
    }

    /**
     * 根据主键删除
     */
    public boolean deleteByKey(Serializable key) throws Exception {
        return actuator().deleteByKey(key) > 0;
    }

    /**
     * 根据主键删除
     */
    public boolean deleteBatchKeys(Collection<Serializable> keys) throws Exception {
        return actuator().deleteBatchKeys(keys) > 0;
    }

    /**
     * 根据条件删除
     */
    public boolean delete(DefaultConditionWrapper<T> wrapper) throws Exception {
        return actuator().deleteSelective(wrapper) > 0;
    }

    /**
     * 根据条件删除
     */
    public boolean delete(Consumer<ConditionWrapper<T>> consumer) throws Exception {
        DefaultConditionWrapper<T> wrapper = Conditions.query(target());
        consumer.accept(wrapper);
        return actuator().deleteSelective(wrapper) > 0;
    }

    /**
     * 根据条件删除
     */
    public boolean deleteLambda(LambdaConditionWrapper<T> wrapper) throws Exception {
        return actuator().deleteSelective(wrapper) > 0;
    }

    /**
     * 根据条件删除
     */
    public boolean deleteLambda(Consumer<LambdaConditionWrapper<T>> consumer) throws Exception {
        LambdaConditionWrapper<T> wrapper = Conditions.lambdaQuery(target());
        consumer.accept(wrapper);
        return actuator().deleteSelective(wrapper) > 0;
    }
    








}
