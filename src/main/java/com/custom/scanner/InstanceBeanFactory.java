package com.custom.scanner;

import com.custom.dbconfig.DbDataSource;
import com.custom.handler.proxy.SqlReaderExecuteProxy;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Xiao-Bai
 * @date 2021/11/24 21:38
 * @desc:用于实例化代理时的工厂对象
 */
public class InstanceBeanFactory<T> implements FactoryBean<T> {

    private DbDataSource dbDataSource;

    private Class<T> interfaceType;

    private SqlReaderExecuteProxy sqlReaderExecuteProxy;

    public InstanceBeanFactory(DbDataSource dbDataSource, SqlReaderExecuteProxy sqlReaderExecuteProxy, Class<T> interfaceType) {
        this.interfaceType = interfaceType;
        this.sqlReaderExecuteProxy = sqlReaderExecuteProxy;
        this.dbDataSource = dbDataSource;
    }

    @Override
    public T getObject() {
        return sqlReaderExecuteProxy.createProxy(interfaceType);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }
}
