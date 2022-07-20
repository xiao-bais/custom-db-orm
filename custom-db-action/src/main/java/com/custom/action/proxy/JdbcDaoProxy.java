package com.custom.action.proxy;

import com.custom.action.dbaction.AbstractSqlExecutor;
import com.custom.action.sqlparser.JdbcAction;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
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
    private final static List<JdbcDaoProxy.CustomizeTypeCache> CUSTOMIZE_TYPE_CACHES = new ArrayList<>();

    public JdbcDaoProxy(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        sqlExecutor = new JdbcActionProxy<>(new JdbcAction(), dbDataSource, dbCustomStrategy).createProxy();
    }

    static {
        Method[] declaredMethods = JdbcAction.class.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            CustomizeTypeCache customizeTypeCache = new CustomizeTypeCache(declaredMethod,
                    declaredMethod.getName(), declaredMethod.getParameterTypes());
            CUSTOMIZE_TYPE_CACHES.add(customizeTypeCache);
        }

    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            }
            String methodName = method.getName();
            CustomizeTypeCache typeCache = CUSTOMIZE_TYPE_CACHES.stream().filter(op -> op.methodName.equals(methodName))
                    .filter(op -> {
                        if (args.length != op.classes.length) {
                            return false;
                        }
                        // 判断两者的参数参数一一对应
                        int targetIndex = 0;
                        int len = op.classes.length;
                        for (int i = 0; i < len; i++) {
                            Class<?> targetClass = op.classes[i];
                            Class<?> thisClass = args[i].getClass();
                            if (targetClass.isAssignableFrom(thisClass)) {
                                targetIndex++;
                            }
                        }
                        return targetIndex == len;
                    })
                    .findFirst().orElseThrow(() ->
                            new CustomCheckException("Unknown execution method : " + methodName));

            return typeCache.targetMethod.invoke(sqlExecutor, args);
        }catch (Exception t) {
            if (t instanceof CustomCheckException) {
                ExThrowsUtil.toCustom(t.getMessage());
            }
            throw t;
        }
    }



    public static class CustomizeTypeCache {

        private final Method targetMethod;

        private final String methodName;

        private final Class<?>[] classes;

        public CustomizeTypeCache(Method targetMethod, String methodName, Class<?>[] classes) {
            this.targetMethod = targetMethod;
            this.methodName = methodName;
            this.classes = classes;
        }
    }


}
