package com.custom.action.condition;

import java.util.function.Consumer;

/**
 * 默认的update编辑对象
 * @author   Xiao-Bai
 * @since  2022/8/6 18:06
 */
public class DefaultUpdateSet<T> extends AbstractUpdateSet<T>
        implements UpdateSet<DefaultUpdateSet<T>, UpdateSqlSet<String, DefaultUpdateSetSqlSetter<T>>, DefaultConditionWrapper<T>> {


    public DefaultUpdateSet(Class<T> entityClass) {
        super(entityClass);
    }


    @Override
    public DefaultUpdateSet<T> setter(boolean condition, UpdateSqlSet<String, DefaultUpdateSetSqlSetter<T>> updateSqlSet) {
        if (condition) {
            setUpdateSetWrapper((DefaultUpdateSetSqlSetter<T>) updateSqlSet);
        }
        return this;
    }

    @Override
    public DefaultUpdateSet<T> setter(boolean condition, Consumer<UpdateSqlSet<String, DefaultUpdateSetSqlSetter<T>>> consumer) {
        if (condition) {
            DefaultUpdateSetSqlSetter<T> updateWrapper = new DefaultUpdateSetSqlSetter<>(thisEntityClass());
            consumer.accept(updateWrapper);
            setUpdateSetWrapper(updateWrapper);
        }
        return this;
    }

    @Override
    public DefaultUpdateSet<T> where(boolean condition, DefaultConditionWrapper<T> wrapper) {
        if (condition) {
            setConditionWrapper(wrapper);
        }
        return this;
    }

    @Override
    public DefaultUpdateSet<T> where(boolean condition, Consumer<DefaultConditionWrapper<T>> consumer) {
        if (condition) {
            DefaultConditionWrapper<T> conditionWrapper = new DefaultConditionWrapper<>(thisEntityClass());
            consumer.accept(conditionWrapper);
            setConditionWrapper(conditionWrapper);
        }
        return this;
    }
}
