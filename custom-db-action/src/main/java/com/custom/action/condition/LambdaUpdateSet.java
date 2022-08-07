package com.custom.action.condition;

import java.util.function.Consumer;

/**
 * @Author Xiao-Bai
 * @Date 2022/8/6 18:06
 * @Desc
 */
public class LambdaUpdateSet<T> extends AbstractUpdateSet<T>
        implements UpdateSet<LambdaUpdateSet<T>, UpdateSqlSet<SFunction<T, ?>, LambdaUpdateSetSqlSetter<T>>, LambdaConditionWrapper<T>> {


    public LambdaUpdateSet(Class<T> entityClass) {
        super(entityClass);
    }


    @Override
    public LambdaUpdateSet<T> setter(boolean condition, UpdateSqlSet<SFunction<T, ?>, LambdaUpdateSetSqlSetter<T>> updateSqlSet) {
        if (condition) {
            setUpdateSetWrapper((LambdaUpdateSetSqlSetter<T>) updateSqlSet);
        }
        return this;
    }

    @Override
    public LambdaUpdateSet<T> setter(boolean condition, Consumer<UpdateSqlSet<SFunction<T, ?>, LambdaUpdateSetSqlSetter<T>>> consumer) {
        if (condition) {
            LambdaUpdateSetSqlSetter<T> updateWrapper = new LambdaUpdateSetSqlSetter<>(thisEntityClass());
            consumer.accept(updateWrapper);
            setUpdateSetWrapper(updateWrapper);
        }
        return this;
    }

    @Override
    public LambdaUpdateSet<T> setNoNeedCondition(boolean condition, UpdateSqlSet<SFunction<T, ?>, LambdaUpdateSetSqlSetter<T>> updateSqlSet) {
        if (condition) {
            setExistCondition(false);
            return this.setter(updateSqlSet);
        }
        return this;
    }

    @Override
    public LambdaUpdateSet<T> setNoNeedCondition(boolean condition, Consumer<UpdateSqlSet<SFunction<T, ?>, LambdaUpdateSetSqlSetter<T>>> consumer) {
        if (condition) {
            setExistCondition(false);
            return this.setter(consumer);
        }
        return this;
    }

    @Override
    public LambdaUpdateSet<T> where(boolean condition, LambdaConditionWrapper<T> wrapper) {
        if (condition) {
            setConditionWrapper(wrapper);
        }
        return this;
    }

    @Override
    public LambdaUpdateSet<T> where(boolean condition, Consumer<LambdaConditionWrapper<T>> consumer) {
        if (condition) {
            LambdaConditionWrapper<T> conditionWrapper = new LambdaConditionWrapper<>(thisEntityClass());
            consumer.accept(conditionWrapper);
            setConditionWrapper(conditionWrapper);
        }
        return this;
    }
}
