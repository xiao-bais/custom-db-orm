package com.custom.action.proxy;

import com.custom.action.dbaction.AbstractSqlExecutor;
import com.custom.action.sqlparser.JdbcAction;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.custom.jdbc.CustomSelectJdbcBasicImpl;
import com.custom.jdbc.CustomUpdateJdbcBasicImpl;
import com.custom.jdbc.select.CustomSelectJdbcBasic;
import com.custom.jdbc.update.CustomUpdateJdbcBasic;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/19 18:53
 * @Desc JdbcDao的接口代理
 */
@SuppressWarnings("unchecked")
public class JdbcDaoProxy<T> implements InvocationHandler, Serializable {

    public T createProxy(Class<T> cls) {
        ClassLoader classLoader = cls.getClassLoader();
        Class<?>[] interfaces = new Class[]{cls};
        return (T) Proxy.newProxyInstance(classLoader, interfaces, this);
    }

    private final AbstractSqlExecutor sqlExecutor;

    public JdbcDaoProxy(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        sqlExecutor = new  JdbcActionProxy<>(new JdbcAction(), dbDataSource, dbCustomStrategy).createProxy();
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            }
            return this.doTruthInvoke(method, args);
        }catch (Exception t) {
            if (t instanceof CustomCheckException) {
                ExThrowsUtil.toCustom(t.getMessage());
            }
            throw t;
        }
    }

    /**
     * 执行
     */
    private Object doTruthInvoke(Method method, Object[] args) {





        return null;

    }


}
