package com.custom.handler.proxy;

import com.custom.annotations.reader.Query;
import com.custom.annotations.reader.Update;
import com.custom.comm.BasicDao;
import com.custom.comm.CustomUtil;
import com.custom.dbconfig.DbDataSource;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;
import com.custom.handler.DbParserFieldHandler;
import com.custom.handler.SqlExecuteHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/19 16:45
 * @Desc：用于读取接口上的注解，并生成代理类去执行路径中文件中的内容
 **/
@SuppressWarnings("unchecked")
@Slf4j
public class SqlReaderExecuteProxy extends SqlExecuteHandler implements InvocationHandler {


    public static <T> T createProxy(Class<T> cls, DbDataSource dbDataSource) {
        ClassLoader classLoader = cls.getClassLoader();
        Class<?>[] interfaces = new Class[]{cls};
        SqlReaderExecuteProxy readerExecuteProxy = new SqlReaderExecuteProxy(dbDataSource);
        return (T) Proxy.newProxyInstance(classLoader, interfaces, readerExecuteProxy);
    }


    public SqlReaderExecuteProxy(DbDataSource dbDataSource) {
        super(dbDataSource, new DbParserFieldHandler());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {

        if(!BasicDao.class.isAssignableFrom(proxy.getClass())) {
            throw new CustomCheckException(String.format(ExceptionConst.EX_Not_INHERITED_BASIC_DAO, method.getDeclaringClass()));
        }

        try {
            if (method.isAnnotationPresent(Query.class)) {
                return doQueryInvoke(method, args);
            } else if (method.isAnnotationPresent(Update.class)) {
                return doUpdateInvoke(method, args);
            }
        } catch (Exception e) {
            if (e instanceof ClassCastException) {
                throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_SUPPORT_USE_BASIC_TYPE, method.getDeclaringClass().getName(), method.getName()));
            }
            throw e;
        }

        throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_ANNO__SQL_READ, method.getName()));
    }


    private Object doUpdateInvoke(Method method, Object[] args) {




        return null;
    }


    private Object doQueryInvoke(Method method, Object[] args) throws Exception {

        Query query = method.getAnnotation(Query.class);
        String sql = query.isPath() ? CustomUtil.loadFiles(query.value()) : query.value();
        Type returnType = method.getGenericReturnType();
        Object[] params = this.handleParamsReplaceSql(sql, method, args, query.isOrder()).toArray();

        if (returnType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) returnType;
            Class<?> typeArgument = (Class<?>) pt.getActualTypeArguments()[0];
            Type type = ((ParameterizedType) returnType).getRawType();
            if (type.equals(List.class)) {
                return query(typeArgument, sql, params);
            } else if (type.equals(Map.class)) {
                return selectOneSql(HashMap.class, sql, params);
            } else if (type.equals(Set.class)) {
                return querySet(typeArgument, sql, params);
            }
        } else if (CustomUtil.judgeDbType(returnType)) {
            return selectOneSql(sql, params);
        } else if (((Class<?>) returnType).isArray()) {
            Class<?> type = ((Class<?>) returnType).getComponentType();
            return queryArray(type, sql, params);
        }
        String typeName = returnType.getTypeName();
        Class<?> cls = Class.forName(typeName);
        return selectOneSql(cls, sql, params);
    }


    /**
     * 将参数中的占位符处理成‘?’
     */
    private List<Object> handleParamsReplaceSql(String sql, Method method, Object[] args, boolean isOrder) {

        List<Object> params = new ArrayList<>();

        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            Object param = args[i];

            //以参数的排列顺序来匹配sql的？位置
            if (isOrder) {
                if (CustomUtil.judgeDbType(type)) {
                    params.add(param);
                } else if (type.equals(List.class)) {
                    params.addAll((List<Object>) param);
                } else if (type.equals(Arrays.class)) {
                    params.addAll(Collections.singletonList(param));
                }else if(type.equals(Set.class)) {
                    Set<Object> paramsSet = (Set<Object>) param;
                    params.addAll(paramsSet);
                }
            }


        }


        return params;

    }


}
