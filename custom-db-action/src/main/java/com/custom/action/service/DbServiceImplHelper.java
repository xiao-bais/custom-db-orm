package com.custom.action.service;

import com.custom.action.core.TableInfoCache;
import com.custom.action.interfaces.TableExecutor;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.ReflectUtil;
import com.custom.jdbc.configuration.DbDataSource;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * service层继承该类，即可拥有增删改查功能
 * @author   Xiao-Bai
 * @since 2023/2/6 22:26
 * @param <T> 实体类映射对象
 */
@SuppressWarnings("unchecked")
public class DbServiceImplHelper<T> implements DbServiceHelper<T> {

    private static final Map<String, Class<?>> CURRENT_TARGET_CACHE = new ConcurrentHashMap<>();


    /**
     * @see DbDataSource#getOrder()
     * 多个数据源的情况下，可重写该方法，指定数据源
     * @return {@link DbDataSource#getOrder()}
     */
    public int order() {
        return Constants.DEFAULT_ONE;
    }

    /**
     * 执行的目标类
     */
    public Class<T> target() {
       return Optional.ofNullable((Class<T>) CURRENT_TARGET_CACHE.get(getClass().toString())).orElseGet(() -> {
            Class<T> thisTarget = ReflectUtil.getThisGenericType(getClass());
            CURRENT_TARGET_CACHE.putIfAbsent(getClass().toString(), thisTarget);
            return thisTarget;
        });
    }

    /**
     * 执行器
     */
    public TableExecutor<T, Serializable> targetTemplate() {
        return TableInfoCache.getTableExecutor(order(), target());
    }


}
