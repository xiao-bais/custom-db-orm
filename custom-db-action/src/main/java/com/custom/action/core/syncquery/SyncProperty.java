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

    //    /**
//     * 要设置的属性
//     * @param setter Model::setXXX
//     */
//    public SyncProperty<T, P> setter(TargetSetter<T, P> setter) {
//        this.setter = setter;
//        return this;
//    }
//
//    /**
//     * 属性查询前的判断，若成立则查询并注入结果
//     */
//    public SyncProperty<T, P> ifCondition(BooleanSupplier supplier) {
//        return ifCondition(x -> supplier.getAsBoolean());
//    }
//
//    /**
//     * 属性查询前的判断，若成立则查询并注入结果
//     * <br/> 可提前根据查询结果来判断是否注入
//     * <br/> example: x -> x.getXXProperty() != null (x为主查询对象)
//     * <br/> example: x -> x.getXXProperty().equals(xxx) (x为主查询对象)
//     */
//    public SyncProperty<T, P> ifCondition(Predicate<T> predicate) {
//        this.predicate = predicate;
//        return this;
//    }
//
//    /**
//     * 属性值的查询条件
//     */
//    public SyncProperty<T, P> condition(ConditionWrapper<P> wrapper) {
//        this.wrapper = wrapper;
//        return this;
//    }
//
//    /**
//     * 消费型的查询条件，用于消费当前主对象来判断条件的加入
//     */
//    public void condition(SyncConsumer<T, P> syncConsumer) {
//        this.syncConsumer = syncConsumer;
//    }
//
//    public Predicate<T> getPredicate() {
//        return predicate;
//    }
//
//    public TargetSetter<T, ?> getSetter() {
//        return setter;
//    }
//
//    public ConditionWrapper<P> getWrapper() {
//        return wrapper;
//    }
//
//    public SyncConsumer<T, P> getSyncConsumer() {
//        return syncConsumer;
//    }
}
