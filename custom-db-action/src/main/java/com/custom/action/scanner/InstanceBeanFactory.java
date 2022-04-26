package com.custom.action.scanner;

import com.custom.action.sqlproxy.ReaderExecutorProxy;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Lazy;

/**
 * @author Xiao-Bai
 * @date 2021/11/24 21:38
 * @desc:用于实例化代理时的工厂对象
 */
public class InstanceBeanFactory<T> implements FactoryBean<T> {

    private final Class<T> interfaceType;

    private final ReaderExecutorProxy readerExecutorProxy;

    @Lazy
    public InstanceBeanFactory(ReaderExecutorProxy readerExecutorProxy, Class<T> interfaceType) {
        this.interfaceType = interfaceType;
        this.readerExecutorProxy = readerExecutorProxy;
    }

    @Override
    public T getObject() {
        return readerExecutorProxy.createProxy(interfaceType);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }
}
