package com.custom.action.core;

import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.configuration.DbGlobalConfig;

import java.io.Serializable;
import java.lang.reflect.*;

/**
 * JdbcDao的接口代理
 * @author   Xiao-Bai
 * @since  2022/7/19 18:53
 */
public class JdbcDaoProxy implements InvocationHandler, Serializable {

    public JdbcDao createProxy() {
        ClassLoader classLoader = JdbcDao.class.getClassLoader();
        Class<?>[] interfaces = new Class[]{JdbcDao.class};
        return (JdbcDao) Proxy.newProxyInstance(classLoader, interfaces, this);
    }

    public JdbcDaoProxy(DbDataSource dbDataSource, DbGlobalConfig globalConfig) {
        this.customMappedHandler = new CustomMappedHandler(dbDataSource, globalConfig);
    }
    private final CustomMappedHandler customMappedHandler;


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }

        try {
            return customMappedHandler.handleExecute(method, args);
        } catch (InvocationTargetException e) {
            Throwable te = e.getTargetException();
            if (te instanceof InvocationTargetException) {
                throw e.getTargetException();
            } else if (te instanceof UndeclaredThrowableException) {
                Throwable undeclaredThrowable = ((UndeclaredThrowableException) te).getUndeclaredThrowable();
                throw undeclaredThrowable.getCause();
            }
            throw te;
        }
    }

}
