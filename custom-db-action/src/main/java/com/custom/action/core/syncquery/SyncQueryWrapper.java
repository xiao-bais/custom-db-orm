package com.custom.action.core.syncquery;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.condition.Conditions;
import com.custom.action.condition.DefaultConditionWrapper;
import com.custom.action.condition.LambdaConditionWrapper;
import com.custom.comm.utils.lambda.TargetSetter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
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
    private ConditionWrapper<T> primaryWrapper;

    private List<SyncProperty<T, ?>> syncProperties;

    public SyncQueryWrapper(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public SyncQueryWrapper<T> primary(Consumer<DefaultConditionWrapper<T>> consumer) {
        DefaultConditionWrapper<T> wrapper = Conditions.query(entityClass);
        consumer.accept(wrapper);
        this.primaryWrapper = wrapper;
        return this;
    }

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
    public <P, Pro> SyncQueryWrapper<T> injectProperty(TargetSetter<T, P> setter, BooleanSupplier condition, ConditionWrapper<Pro> wrapper) {
        SyncProperty<T, Pro> syncProperty = new SyncProperty<>();
        syncProperty.setCondition(x -> condition.getAsBoolean());
        syncProperty.setSetter(setter);
        syncProperty.setWrapper(wrapper);
        this.addPropertyInjector(syncProperty);
        return this;
    }

    public <P, Pro> SyncQueryWrapper<T> injectProperty(TargetSetter<T, P> setter, ConditionWrapper<Pro> wrapper) {
        return injectProperty(setter, () -> true, wrapper);
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
    public <P, Pro> SyncQueryWrapper<T> injectProperty(TargetSetter<T, P> setter, BooleanSupplier condition, SyncFunction<T, Pro> syncFunction) {
        SyncProperty<T, Pro> syncProperty = new SyncProperty<>();
        syncProperty.setCondition(x -> condition.getAsBoolean());
        syncProperty.setSetter(setter);
        syncProperty.setSyncFunction(syncFunction);
        this.addPropertyInjector(syncProperty);
        return this;
    }

    public <P, Pro> SyncQueryWrapper<T> injectProperty(TargetSetter<T, P> setter, SyncFunction<T, Pro> syncConsumer) {
        return injectProperty(setter, () -> true, syncConsumer);
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
    public <P, Pro> SyncQueryWrapper<T> injectProperty(TargetSetter<T, P> setter, Predicate<T> condition, SyncFunction<T, Pro> syncFunction) {
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
    public <P, Pro> SyncQueryWrapper<T> injectProperty(TargetSetter<T, P> setter, Predicate<T> condition, ConditionWrapper<Pro> wrapper) {
        SyncProperty<T, Pro> syncProperty = new SyncProperty<>();
        syncProperty.setCondition(condition);
        syncProperty.setSetter(setter);
        syncProperty.setWrapper(wrapper);
        this.addPropertyInjector(syncProperty);
        return this;
    }


    private void addPropertyInjector(SyncProperty<T, ?> property) {
        if (this.syncProperties == null) {
            this.syncProperties = new ArrayList<>();
        }
        this.syncProperties.add(property);
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public ConditionWrapper<T> getPrimaryWrapper() {
        return primaryWrapper;
    }

    public List<SyncProperty<T, ?>> getSyncProperties() {
        return syncProperties;
    }

    public void setPrimaryWrapper(ConditionWrapper<T> primaryWrapper) {
        this.primaryWrapper = primaryWrapper;
    }
}
