package com.custom.action.condition;

/**
 * @Author Xiao-Bai
 * @Date 2022/8/6 23:43
 * @Desc
 */
public abstract class AbstractUpdateSet<T> {

    /**
     * sql set 设置器
     */
    private UpdateSetWrapper<T> updateSetWrapper;
    /**
     * sql 条件构造器
     */
    private ConditionWrapper<T> conditionWrapper;
    /**
     * 实体class对象
     */
    private final Class<T> entityClass;


    public AbstractUpdateSet(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public UpdateSetWrapper<T> getUpdateSetWrapper() {
        return updateSetWrapper;
    }

    protected void setUpdateSetWrapper(UpdateSetWrapper<T> updateSetWrapper) {
        this.updateSetWrapper = updateSetWrapper;
    }

    public ConditionWrapper<T> getConditionWrapper() {
        return conditionWrapper;
    }

    protected void setConditionWrapper(ConditionWrapper<T> conditionWrapper) {
        this.conditionWrapper = conditionWrapper;
    }

    public Class<T> thisEntityClass() {
        return entityClass;
    }
}
