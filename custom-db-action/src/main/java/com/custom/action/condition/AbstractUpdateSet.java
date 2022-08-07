package com.custom.action.condition;

/**
 * @Author Xiao-Bai
 * @Date 2022/8/6 23:43
 * @Desc
 */
public abstract class AbstractUpdateSet<T> {

    /**
     *
     */
    private UpdateSetWrapper<T> updateSetWrapper;
    private ConditionWrapper<T> conditionWrapper;
    private final Class<T> entityClass;
    private boolean existCondition = true;


    public AbstractUpdateSet(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public UpdateSetWrapper<T> getUpdateSetWrapper() {
        return updateSetWrapper;
    }

    public void setUpdateSetWrapper(UpdateSetWrapper<T> updateSetWrapper) {
        this.updateSetWrapper = updateSetWrapper;
    }

    public ConditionWrapper<T> getConditionWrapper() {
        return conditionWrapper;
    }

    public void setConditionWrapper(ConditionWrapper<T> conditionWrapper) {
        this.conditionWrapper = conditionWrapper;
    }

    public Class<T> thisEntityClass() {
        return entityClass;
    }

    public boolean isExistCondition() {
        return existCondition;
    }

    public void setExistCondition(boolean existCondition) {
        this.existCondition = existCondition;
    }
}
