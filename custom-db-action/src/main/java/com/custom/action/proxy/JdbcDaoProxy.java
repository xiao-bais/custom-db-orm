package com.custom.action.proxy;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.JdbcAction;
import com.custom.action.core.JdbcDao;
import com.custom.action.core.SqlExecutor;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.configuration.DbGlobalConfig;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    private final SqlExecutor sqlExecutor;
    private final static Map<Class<?>, Class<?>> PRIMITIVE_MAPPED;
    private final static Map<Method, Method> TARGET_METHOD_CACHE;

    public JdbcDaoProxy(DbDataSource dbDataSource, DbGlobalConfig globalConfig) {
        sqlExecutor = new JdbcActionProxy(new JdbcAction(), dbDataSource, globalConfig).createProxy();
    }

    static {
        TARGET_METHOD_CACHE = new ConcurrentHashMap<>();
        PRIMITIVE_MAPPED = new HashMap<>(8);
        PRIMITIVE_MAPPED.put(Integer.TYPE, Integer.class);
        PRIMITIVE_MAPPED.put(Long.TYPE, Long.class);
        PRIMITIVE_MAPPED.put(Short.TYPE, Short.class);
        PRIMITIVE_MAPPED.put(Double.TYPE, Double.class);
        PRIMITIVE_MAPPED.put(Float.TYPE, Float.class);
        PRIMITIVE_MAPPED.put(Byte.TYPE, Byte.class);
        PRIMITIVE_MAPPED.put(Character.TYPE, Character.class);
        PRIMITIVE_MAPPED.put(Boolean.TYPE, Boolean.class);
    }




    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        Method targetExecMethod = TARGET_METHOD_CACHE.get(method);
        if (targetExecMethod != null) {
            return targetExecMethod.invoke(sqlExecutor, args);
        }
        String methodName = method.getName();
        Method[] declaredMethods = JdbcAction.class.getDeclaredMethods();
        targetExecMethod = Arrays.stream(declaredMethods)
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
                        } else {
                            thisClass = args[i].getClass();
                        }
                        if (ConditionWrapper.class.isAssignableFrom(thisClass)) {
                            return !Object.class.equals(targetClass);
                        }
                        // target 目标参数类型
                        // this 本次传递的参数类型
                        if (targetClass.isAssignableFrom(thisClass)) {
                            targetIndex++;
                        } else if (targetClass.isPrimitive()) {
                            Class<?> primitiveClass = PRIMITIVE_MAPPED.get(targetClass);
                            if (thisClass.equals(primitiveClass)) {
                                targetIndex++;
                            }
                        }
                    }
                    return targetIndex == len;
                })
                .findFirst().orElseThrow(() ->
                        new CustomCheckException("Unknown execution method : " + methodName)
                );
        try {
            TARGET_METHOD_CACHE.put(method, targetExecMethod);
            return targetExecMethod.invoke(sqlExecutor, args);
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
