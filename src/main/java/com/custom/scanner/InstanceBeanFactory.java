package com.custom.scanner;

import com.custom.dbconfig.DbDataSource;
import com.custom.handler.proxy.SqlReaderExecuteProxy;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author Xiao-Bai
 * @date 2021/11/24 21:38
 * @desc:
 */
public class InstanceBeanFactory<T> implements FactoryBean<T> {

    private DbDataSource dbDataSource;

    private Class<T> interfaceType;

    public InstanceBeanFactory(Class<T> interfaceType, DbDataSource dbDataSource) {
        this.interfaceType = interfaceType;
        this.dbDataSource = dbDataSource;
    }

    @Override
    public T getObject() {
        return new SqlReaderExecuteProxy(dbDataSource).createProxy(interfaceType);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }
}
