package com.custom.proxy;

import com.custom.comm.annotations.mapper.Query;
import com.custom.comm.annotations.mapper.SqlMapper;
import com.custom.comm.annotations.mapper.SqlPath;
import com.custom.comm.annotations.mapper.Update;
import com.custom.comm.enums.ExecuteMethod;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.jdbc.configuration.CustomConfigHelper;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.utils.DbConnGlobal;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author  Xiao-Bai
 * @since  2021/11/19 16:45
 * @Desc 用于读取接口上的注解，并生成代理类去执行路径中（文件中）的内容
 **/
@SuppressWarnings("unchecked")
@Slf4j
public class InterfacesProxyExecutor implements InvocationHandler {

    public <T> T createProxy(Class<T> cls) {
        ClassLoader classLoader = cls.getClassLoader();
        Class<?>[] interfaces = new Class[]{cls};
        targetClassName = cls.getName();
        SqlMapper sqlMapper = cls.getAnnotation(SqlMapper.class);
        CustomConfigHelper configHelper = DbConnGlobal.getConfigHelper(sqlMapper.order());
        this.executorFactory = new JdbcExecutorFactory(configHelper.getDbDataSource(),  configHelper.getDbGlobalConfig());

        return (T) Proxy.newProxyInstance(classLoader, interfaces, this);
    }

    private String targetClassName;
    private JdbcExecutorFactory executorFactory;

    public InterfacesProxyExecutor() {

    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {

        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        Object result;
        try {
            result = this.doInvokeOperation(method, args);
        }catch (IllegalArgumentException e) {
            log.error("illegal parameter: {}", e.getMessage());
            throw e;
        }catch (CustomCheckException ex) {
            log.error("custom check exception: {}", ex.getMessage());
            throw ex;
        }
        return result;
    }

    /**
    * 执行具体代理方法
    */
    private Object doInvokeOperation(Method method, Object[] args) throws Exception {

        AbstractProxyHandler proxyHandler = null;
        Class<?> execClass = method.getDeclaringClass();
        if (!execClass.isAnnotationPresent(SqlMapper.class)) {
            throw new CustomCheckException("Execution error, possibly because '%s' does not inherit com.custom.comm.BasicDao or this interface is not annotated with @SqlMapper", targetClassName);
        }

        // do Query
        if (method.isAnnotationPresent(Query.class)) {
            Query query = method.getAnnotation(Query.class);
            proxyHandler = new SelectProxyHandler(executorFactory, args, query.value(), method);
        }

        // do Update
        else if (method.isAnnotationPresent(Update.class)) {
            Update update = method.getAnnotation(Update.class);
            proxyHandler = new UpdateProxyHandler(executorFactory, args, update.value(), method);
        }

        // do sqlPath(select or update)
        else if (method.isAnnotationPresent(SqlPath.class)) {
            SqlPath sqlPath = method.getAnnotation(SqlPath.class);
            ExecuteMethod execType = sqlPath.method();
            String sql = new ClearNotesOnSqlHandler(sqlPath.value()).loadSql();
            if (execType == ExecuteMethod.SELECT) {
                proxyHandler = new SelectProxyHandler(executorFactory, args, sql, method);
            } else if (execType == ExecuteMethod.UPDATE
                    || execType == ExecuteMethod.DELETE
                    || execType == ExecuteMethod.INSERT) {

                proxyHandler = new UpdateProxyHandler(executorFactory, args, sql, method);
            }
        } else
            throw new CustomCheckException("The '@Update' or '@Query' or '@SqlPath' annotation was not found on the method : %s.%s()", targetClassName, method.getName());

        if (proxyHandler == null) {
            throw new CustomCheckException("未知的执行类型");
        }
        proxyHandler.prepareParamsParsing();
        return proxyHandler.execute();
    }


}
