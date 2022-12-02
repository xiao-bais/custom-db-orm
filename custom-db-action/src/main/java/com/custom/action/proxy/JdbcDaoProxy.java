package com.custom.action.proxy;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.dbaction.AbstractSqlExecutor;
import com.custom.action.core.JdbcAction;
import com.custom.action.core.JdbcDao;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/19 18:53
 * @Desc JdbcDao的接口代理
 */
public class JdbcDaoProxy implements InvocationHandler, Serializable {

    public JdbcDao createProxy() {
        ClassLoader classLoader = JdbcDao.class.getClassLoader();
        Class<?>[] interfaces = new Class[]{JdbcDao.class};
        return (JdbcDao) Proxy.newProxyInstance(classLoader, interfaces, this);
    }

    private final AbstractSqlExecutor sqlExecutor;
    private final static List<Method> CUSTOMIZE_METHOD_CACHES;

    public JdbcDaoProxy(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        sqlExecutor = new JdbcActionProxy(new JdbcAction(), dbDataSource, dbCustomStrategy).createProxy();
    }

    static {
        Method[] declaredMethods = JdbcAction.class.getDeclaredMethods();
        CUSTOMIZE_METHOD_CACHES = new ArrayList<>((Arrays.asList(declaredMethods)));
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        String methodName = method.getName();
        Method typeCache = CUSTOMIZE_METHOD_CACHES.stream()
                .filter(op -> op.getName().equals(methodName))
                .filter(op -> {
                    Class<?>[] parameterTypes = op.getParameterTypes();
                    if (args.length != parameterTypes.length) {
                        return false;
                    }
                    // 判断两者的参数类型一一对应
                    int targetIndex = 0;
                    int len = parameterTypes.length;
                    for (int i = 0; i < len; i++) {
                        Class<?> targetClass = parameterTypes[i];
                        Class<?> thisClass;
                        if (args[i] == null) {
                            thisClass = targetClass;
                        }else {
                            thisClass = args[i].getClass();
                        }
                        if (ConditionWrapper.class.isAssignableFrom(thisClass)) {
                            return !Object.class.equals(targetClass);
                        }
                        if (targetClass.isAssignableFrom(thisClass)) {
                            targetIndex++;
                        }
                    }
                    return targetIndex == len;
                })
                .findFirst().orElseThrow(() ->
                        new CustomCheckException("Unknown execution method : " + methodName)
                );

        return typeCache.invoke(sqlExecutor, args);
    }

}
