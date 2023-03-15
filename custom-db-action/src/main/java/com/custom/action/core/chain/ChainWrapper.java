package com.custom.action.core.chain;

import com.custom.action.condition.Conditions;
import com.custom.action.condition.DefaultConditionWrapper;
import com.custom.action.condition.LambdaConditionWrapper;
import com.custom.action.core.DoTargetExecutor;
import com.custom.action.core.TableInfoCache;
import com.custom.jdbc.session.JdbcSqlSessionFactory;

import java.util.function.Consumer;

/**
 * @author Xiao-Bai
 * @since 2023/3/7 23:03
 */
public class ChainWrapper<T> {

    private final Class<T> entityClass;
    private final JdbcSqlSessionFactory sqlSessionFactory;

    public ChainWrapper(Class<T> entityClass, JdbcSqlSessionFactory sqlSessionFactory) {
        this.entityClass = entityClass;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public DoTargetExecutor<T> where(Consumer<DefaultConditionWrapper<T>> consumer) {
        DefaultConditionWrapper<T> conditionWrapper = Conditions.query(entityClass);
        consumer.accept(conditionWrapper);
        return DoTargetExecutor.build(conditionWrapper, TableInfoCache.getTableExecutor(sqlSessionFactory, entityClass));
    }

    public DoTargetExecutor<T> where(DefaultConditionWrapper<T> conditionWrapper) {
        return DoTargetExecutor.build(conditionWrapper, TableInfoCache.getTableExecutor(sqlSessionFactory, entityClass));
    }

    public DoTargetExecutor<T> whereEx(Consumer<LambdaConditionWrapper<T>> consumer) {
        LambdaConditionWrapper<T> conditionWrapper = Conditions.lambdaQuery(entityClass);
        consumer.accept(conditionWrapper);
        return DoTargetExecutor.build(conditionWrapper, TableInfoCache.getTableExecutor(sqlSessionFactory, entityClass));
    }

    public DoTargetExecutor<T> whereEx(LambdaConditionWrapper<T> conditionWrapper) {
        return DoTargetExecutor.build(conditionWrapper, TableInfoCache.getTableExecutor(sqlSessionFactory, entityClass));
    }





}
