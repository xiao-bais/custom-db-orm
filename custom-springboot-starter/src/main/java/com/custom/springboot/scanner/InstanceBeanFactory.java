package com.custom.springboot.scanner;

import com.custom.proxy.InterfacesProxyExecutor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Lazy;

/**
 * @author Xiao-Bai
 * @date 2021/11/24 21:38
 * @desc:用于实例化代理时的工厂对象
 */
public class InstanceBeanFactory<T> implements FactoryBean<T> {

    private final Class<T> interfaceType;

    @Lazy
    public InstanceBeanFactory(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public T getObject() {
        return new InterfacesProxyExecutor().createProxy(interfaceType);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }
}
