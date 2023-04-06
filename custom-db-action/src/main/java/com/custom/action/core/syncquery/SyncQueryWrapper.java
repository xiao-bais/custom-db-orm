package com.custom.action.core.syncquery;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.condition.Conditions;
import com.custom.action.condition.DefaultConditionWrapper;
import com.custom.action.condition.LambdaConditionWrapper;
import com.custom.comm.utils.lambda.TargetSetter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 同步查询结果包装
 * <p> 当对象中存在一对一或者一对多的情况时，可在一次查询中实现 </p>
 *
 * @author Xiao-Bai
 * @since 2023/3/26 23:25
 */
public class SyncQueryWrapper<T> {

    private final Class<T> entityClass;
    /**
     * 主对象查询条件
     */
    private ConditionWrapper<T> primaryWrapper;

    /**
     * 同步的属性
     */
    private List<SyncProperty<T, ?>> syncPropertyList;

    public SyncQueryWrapper(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * 查询主对象
     * @param consumer 消费型普通条件构造器
     */
    public SyncQueryWrapper<T> primary(Consumer<DefaultConditionWrapper<T>> consumer) {
        DefaultConditionWrapper<T> wrapper = Conditions.query(entityClass);
        consumer.accept(wrapper);
        this.primaryWrapper = wrapper;
        return this;
    }

    /**
     * 查询主对象
     * @param consumer 消费型lambda条件构造器
     */
    public SyncQueryWrapper<T> primaryEx(Consumer<LambdaConditionWrapper<T>> consumer) {
        LambdaConditionWrapper<T> wrapper = Conditions.lambdaQuery(entityClass);
        consumer.accept(wrapper);
        this.primaryWrapper = wrapper;
        return this;
    }

    /**
     * 注入属性的查询结果
     * @param setter Model::setXXX (属性的set方法)
     * @param condition ( ) -> a != null (执行的条件，用于判断该次注入是否执行)
     * @param wrapper <code>Conditions.query(XXModel.class).eq("xxColumn", "123")</code> (查询条件)
     * @param <P>
     * @param <Pro>
     * @return
     */
    public <P, Pro> SyncQueryWrapper<T> property(TargetSetter<T, P> setter, boolean condition, ConditionWrapper<Pro> wrapper) {
        SyncProperty<T, Pro> syncProperty = new SyncProperty<>();
        syncProperty.setCondition(x -> condition);
        syncProperty.setSetter(setter);
        syncProperty.setWrapper(wrapper);
        this.addPropertyInjector(syncProperty);
        return this;
    }

    public <P, Pro> SyncQueryWrapper<T> property(TargetSetter<T, P> setter, ConditionWrapper<Pro> wrapper) {
        return property(setter,  true, wrapper);
    }


    /**
     * 注入属性的查询结果
     * @param setter Model::setXXX (属性的set方法)
     * @param condition ( ) -> a != null (执行的条件，用于判断该次注入是否执行)
     * @param syncFunction <code> res -> Conditions.query(XXModel.class).eq("xxColumn", "123")</code> (查询条件, 其中res为主对象(即 T)，只是查询之前的提前消费)
     * @param <P>
     * @param <Pro>
     * @return
     */
    public <P, Pro> SyncQueryWrapper<T> property(TargetSetter<T, P> setter, boolean condition, SyncFunction<T, Pro> syncFunction) {
        SyncProperty<T, Pro> syncProperty = new SyncProperty<>();
        syncProperty.setCondition(x -> condition);
        syncProperty.setSetter(setter);
        syncProperty.setSyncFunction(syncFunction);
        this.addPropertyInjector(syncProperty);
        return this;
    }

    public <P, Pro> SyncQueryWrapper<T> property(TargetSetter<T, P> setter, SyncFunction<T, Pro> syncConsumer) {
        return property(setter, true, syncConsumer);
    }

    /**
     * 注入属性的查询结果
     * @param setter Model::setXXX (属性的set方法)
     * @param condition res -> a != null (执行的条件, 其中res为主对象(即 T), 用于判断该次注入是否执行)
     * @param syncFunction <code> res -> Conditions.query(XXModel.class).eq("xxColumn", "123")</code> (查询条件, 其中res为主对象(即 T)，只是查询之前的提前消费)
     * @param <P>
     * @param <Pro>
     * @return
     */
    public <P, Pro> SyncQueryWrapper<T> property(TargetSetter<T, P> setter, Predicate<T> condition, SyncFunction<T, Pro> syncFunction) {
        SyncProperty<T, Pro> syncProperty = new SyncProperty<>();
        syncProperty.setCondition(condition);
        syncProperty.setSetter(setter);
        syncProperty.setSyncFunction(syncFunction);
        this.addPropertyInjector(syncProperty);
        return this;
    }

    /**
     * 注入属性的查询结果
     * @param setter Model::setXXX (属性的set方法)
     * @param condition res -> a != null (执行的条件, 其中res为主对象(即 T), 用于判断该次注入是否执行)
     * @param wrapper <code> Conditions.query(XXModel.class).eq("xxColumn", "123")</code> (查询条件)
     * @param <P>
     * @param <Pro>
     * @return
     */
    public <P, Pro> SyncQueryWrapper<T> property(TargetSetter<T, P> setter, Predicate<T> condition, ConditionWrapper<Pro> wrapper) {
        SyncProperty<T, Pro> syncProperty = new SyncProperty<>();
        syncProperty.setCondition(condition);
        syncProperty.setSetter(setter);
        syncProperty.setWrapper(wrapper);
        this.addPropertyInjector(syncProperty);
        return this;
    }


    private void addPropertyInjector(SyncProperty<T, ?> property) {
        if (this.syncPropertyList == null) {
            this.syncPropertyList = new ArrayList<>();
        }
        this.syncPropertyList.add(property);
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public ConditionWrapper<T> getPrimaryWrapper() {
        return primaryWrapper;
    }

    public List<SyncProperty<T, ?>> getSyncPropertyList() {
        return syncPropertyList;
    }

    public void setPrimaryWrapper(ConditionWrapper<T> primaryWrapper) {
        this.primaryWrapper = primaryWrapper;
    }
}
