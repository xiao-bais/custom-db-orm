package com.custom.action.core.syncquery;

import com.custom.action.condition.ConditionWrapper;
import com.custom.comm.utils.lambda.TargetSetter;

import java.util.function.Predicate;

/**
 * @author Xiao-Bai
 * @since 2023/3/27 15:07
 */
public class SyncProperty<T, P> {

    private Predicate<T> condition;

    private TargetSetter<T, ?> setter;

    private ConditionWrapper<?> wrapper;

    private SyncFunction<T, P> syncFunction;

    public SyncProperty() {

    }

    public Predicate<T> getCondition() {
        return condition;
    }

    public void setCondition(Predicate<T> condition) {
        this.condition = condition;
    }

    public TargetSetter<T, ?> getSetter() {
        return setter;
    }

    public void setSetter(TargetSetter<T, ?> setter) {
        this.setter = setter;
    }

    public ConditionWrapper<?> getWrapper() {
        return wrapper;
    }

    public void setWrapper(ConditionWrapper<?> wrapper) {
        this.wrapper = wrapper;
    }

    public SyncFunction<T, P> getSyncFunction() {
        return syncFunction;
    }

    public void setSyncFunction(SyncFunction<T, P> syncFunction) {
        this.syncFunction = syncFunction;
    }


}
