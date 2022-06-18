package com.custom.proxy;

import com.custom.comm.BasicDao;
import com.custom.comm.RexUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.annotations.mapper.Query;
import com.custom.comm.annotations.mapper.SqlMapper;
import com.custom.comm.annotations.mapper.SqlPath;
import com.custom.comm.annotations.mapper.Update;
import com.custom.comm.enums.ExecuteMethod;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.custom.jdbc.CustomSelectJdbcBasicImpl;
import com.custom.jdbc.CustomUpdateJdbcBasicImpl;
import com.custom.jdbc.select.CustomSelectJdbcBasic;
import com.custom.jdbc.update.CustomUpdateJdbcBasic;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/19 16:45
 * @Desc 用于读取接口上的注解，并生成代理类去执行路径中（文件中）的内容
 **/
@SuppressWarnings("unchecked")
@Slf4j
public class InterfacesProxyExecutor implements InvocationHandler {

    public <T> T createProxy(Class<T> cls) {
        ClassLoader classLoader = cls.getClassLoader();
        Class<?>[] interfaces = new Class[]{cls};
        targetClassName = cls.getName();
        return (T) Proxy.newProxyInstance(classLoader, interfaces, this);
    }

    private String targetClassName;
    private final CustomSelectJdbcBasic selectJdbc;
    private final CustomUpdateJdbcBasic updateJdbc;

    public InterfacesProxyExecutor(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        selectJdbc = new CustomSelectJdbcBasicImpl(dbDataSource, dbCustomStrategy);
        updateJdbc = new CustomUpdateJdbcBasicImpl(dbDataSource, dbCustomStrategy);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {

        if(method.getName().equals("toString")) {
            return this.toString();
        }
        Object result;
        try {
            result = doInvoke(method, args);
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
    private Object doInvoke(Method method, Object[] args) throws Exception {

        AbstractProxyHandler proxyHandler = null;
        Class<?> execClass = method.getDeclaringClass();
        if (!BasicDao.class.isAssignableFrom(execClass) && !execClass.isAnnotationPresent(SqlMapper.class)) {
            ExThrowsUtil.toCustom("Execution error, possibly because '%s' does not inherit com.custom.comm.BasicDao or this interface is not annotated with @SqlMapper", targetClassName);
        }

        // do Query
        if (method.isAnnotationPresent(Query.class)) {
            Query query = method.getAnnotation(Query.class);
            checkIllegalParam(method.getName(), query.order(), query.value());
            proxyHandler = new SelectProxyHandler(selectJdbc, args, query.value(), method);
        }

        // do Update
        else if (method.isAnnotationPresent(Update.class)) {
            Update update = method.getAnnotation(Update.class);
            checkIllegalParam(method.getName(), update.order(), update.value());
            proxyHandler = new UpdateProxyHandler(updateJdbc,  args, update.value(), method);
        }

        // do sqlPath(select or update)
        else if (method.isAnnotationPresent(SqlPath.class)) {
            SqlPath sqlPath = method.getAnnotation(SqlPath.class);
            ExecuteMethod execType = sqlPath.method();
            String sql = new ClearNotesOnSqlHandler(sqlPath.value()).loadSql();
            checkIllegalParam(method.getName(), sqlPath.order(), sql);
            if (execType == ExecuteMethod.SELECT) {
                proxyHandler = new SelectProxyHandler(selectJdbc, args, sql, method);
            }
            else if (execType == ExecuteMethod.UPDATE
                    || execType == ExecuteMethod.DELETE
                    || execType == ExecuteMethod.INSERT) {

                proxyHandler = new UpdateProxyHandler(updateJdbc, args, sql, method);
            }
        }else ExThrowsUtil.toCustom("The '@Update' or '@Query' or '@SqlPath' annotation was not found on the method : %s.%s()", targetClassName, method.getName());

        if (proxyHandler == null) {
            ExThrowsUtil.toCustom("未知的执行类型");
        }
        proxyHandler.prepareParamsParsing();
        return proxyHandler.execute();
    }

    /**
     * 检验参数合法性
     */
    private void checkIllegalParam(String methodName, boolean order, String sql) {

        if(RexUtil.hasRegex(sql, RexUtil.sql_set_param) && sql.contains(SymbolConstant.QUEST)) {
            log.error("如果order为true，仅支持使用 \"?\"  如果order为false 仅支持使用 \"#{ }\" 来设置参数");
            log.error("Error Method ==> {}", methodName);
            ExThrowsUtil.toCustom("The SQL cannot be resolved '%s'", sql);
        }
        if(order && RexUtil.hasRegex(sql, RexUtil.sql_set_param)) {
            log.error("Error Method ==> {}", methodName);
            ExThrowsUtil.toCustom("方法注解上建议使用 order = false");

        }else if(!order && sql.contains(SymbolConstant.QUEST)) {
                log.error("Error Method ==> {}", methodName);
                ExThrowsUtil.toCustom("方法注解上建议使用 order = true");
            }
    }


}
