package com.custom.jdbc.transaction;

import com.custom.configuration.DbConnection;
import com.custom.jdbc.back.BackResult;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * @author Xiao-Bai
 * @date 2022/10/9 20:34
 * @desc
 */
@SuppressWarnings("unchecked")
public class BackResultTransactionProxy<T> implements InvocationHandler {


    public BackResultTransactionProxy() {

//        DbConnection.currMap.get()
    }

    public BackResult.Back<T> getBack() {
        ClassLoader classLoader = BackResult.Back.class.getClassLoader();
        Class<?>[] interfaces = new Class[]{BackResult.Back.class};
        return (BackResult.Back<T>) Proxy.newProxyInstance(classLoader, interfaces, this);
    }

    private Connection connection;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }

        System.out.println("method = " + method.getName());

        return null;
    }
}
