package com.custom.proxy;

import com.custom.annotations.mapper.Query;
import com.custom.annotations.mapper.SqlMapper;
import com.custom.annotations.mapper.SqlPath;
import com.custom.annotations.mapper.Update;
import com.custom.comm.BasicDao;
import com.custom.comm.CustomUtil;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.ExecuteMethod;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;
import com.custom.dbaction.SqlExecuteAction;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/19 16:45
 * @Desc：用于读取接口上的注解，并生成代理类去执行路径中（文件中）的内容
 **/
@SuppressWarnings("unchecked")
@Slf4j
public class SqlReaderExecuteProxy extends SqlExecuteAction implements InvocationHandler {

    public <T> T createProxy(Class<T> cls) {
        ClassLoader classLoader = cls.getClassLoader();
        Class<?>[] interfaces = new Class[]{cls};
        target = cls.getName();
        return (T) Proxy.newProxyInstance(classLoader, interfaces, this);
    }

    private String target;

    public SqlReaderExecuteProxy(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        super(dbDataSource, dbCustomStrategy);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {

        if(method.getName().equals("toString")) {
            return this + "";
        }
        Object result;
        try {
            result = doInvoke(proxy, method, args);
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
    private Object doInvoke(Object proxy, Method method, Object[] args) throws Exception {

        if (!BasicDao.class.isAssignableFrom(method.getDeclaringClass()) && !method.getDeclaringClass().isAnnotationPresent(SqlMapper.class)) {
            throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_INHERITED_BASIC_DAO, target));
        }

        if (method.isAnnotationPresent(Query.class)) {
            Query query = method.getAnnotation(Query.class);
            return doPrepareExecuteQuery(method, args, query.value(), query.isOrder());

        } else if (method.isAnnotationPresent(Update.class)) {
            Update update = method.getAnnotation(Update.class);
            return doPrepareExecuteUpdate(method, args, update.value(), update.isOrder());

        } else if (method.isAnnotationPresent(SqlPath.class)) {
            SqlPath sqlPath = method.getAnnotation(SqlPath.class);
            ExecuteMethod execType = sqlPath.method();
            String sql = CustomUtil.loadFiles(sqlPath.value());

            if (execType == ExecuteMethod.SELECT) {
                return doPrepareExecuteQuery(method, args, sql, sqlPath.isOrder());
            } else if (execType == ExecuteMethod.UPDATE || execType == ExecuteMethod.DELETE || execType == ExecuteMethod.INSERT) {
                return doPrepareExecuteUpdate(method, args, sql, sqlPath.isOrder());
            }
            return null;
        }
        throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_ANNO__SQL_READ, method.getName()));
    }


    /**
    * 执行更新代理
    */
    private Object doPrepareExecuteUpdate(Method method, Object[] args, String sql, boolean isOrder) throws Exception {

        String methodName = String.format(" %s.%s() ", method.getDeclaringClass().getName(), method.getName());

        if(sql.contains(SymbolConst.PREPARE_BEGIN_REX_1) && isOrder) {
            throw new CustomCheckException(methodName + ExceptionConst.EX_USE_ORDER_FALSE);
        }
        if(sql.contains(SymbolConst.QUEST) && !isOrder) {
            throw new CustomCheckException(methodName + ExceptionConst.EX_USE_ORDER_TRUE);
        }
        ParameterCustomParserModel parameterCustomParserModel = new ParameterCustomParserModel(sql, method, args);
        if(isOrder) {
            parameterCustomParserModel.prepareOrderParams();
        }else {
            parameterCustomParserModel.prepareDisorderParams();
        }
        List<Object> paramValues = parameterCustomParserModel.getParamResList();
        return executeUpdate(parameterCustomParserModel.getPrepareSql(), paramValues.toArray());
    }


    /**
    * 执行查询代理
    */
    private Object doPrepareExecuteQuery(Method method, Object[] args, String sql, boolean isOrder) throws Exception {
        Type returnType = method.getGenericReturnType();
        ParameterCustomParserModel parameterCustomParserModel = new ParameterCustomParserModel(sql, method, args);
        if(isOrder) {
            parameterCustomParserModel.prepareOrderParams();
        }else {
            parameterCustomParserModel.prepareDisorderParams();
        }

        List<Object> paramValues = parameterCustomParserModel.getParamResList();
        sql = parameterCustomParserModel.getPrepareSql();
        Object[] params = paramValues.toArray();

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
        } else if (CustomUtil.isBasicType(returnType)) {
            return selectOneSql(sql, params);

        } else if (((Class<?>) returnType).isArray()) {
            Class<?> type = ((Class<?>) returnType).getComponentType();
            return queryArray(type, sql, method.getDeclaringClass().getName(), method.getName(), params);
        }
        String typeName = returnType.getTypeName();
        Class<?> cls = Class.forName(typeName);
        return selectOneSql(cls, sql, params);
    }

}
