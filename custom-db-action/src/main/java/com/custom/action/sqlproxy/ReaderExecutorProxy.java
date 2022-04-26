package com.custom.action.sqlproxy;

import com.custom.action.dbaction.SqlExecuteAction;
import com.custom.comm.BasicDao;
import com.custom.comm.CustomUtil;
import com.custom.comm.RexUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.annotations.mapper.Query;
import com.custom.comm.annotations.mapper.SqlMapper;
import com.custom.comm.annotations.mapper.SqlPath;
import com.custom.comm.annotations.mapper.Update;
import com.custom.comm.enums.ExecuteMethod;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.comm.exceptions.ExceptionConst;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/19 16:45
 * @Desc：用于读取接口上的注解，并生成代理类去执行路径中（文件中）的内容
 **/
@SuppressWarnings("unchecked")
@Slf4j
public class ReaderExecutorProxy extends SqlExecuteAction implements InvocationHandler {

    public <T> T createProxy(Class<T> cls) {
        ClassLoader classLoader = cls.getClassLoader();
        Class<?>[] interfaces = new Class[]{cls};
        targetClassName = cls.getName();
        return (T) Proxy.newProxyInstance(classLoader, interfaces, this);
    }

    private String targetClassName;

    public ReaderExecutorProxy(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        super(dbDataSource, dbCustomStrategy);
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

        Class<?> execClass = method.getDeclaringClass();
        if (!BasicDao.class.isAssignableFrom(execClass) && !execClass.isAnnotationPresent(SqlMapper.class)) {
            throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_INHERITED_BASIC_DAO, targetClassName));
        }

        if (method.isAnnotationPresent(Query.class)) {
            Query query = method.getAnnotation(Query.class);
            return doPrepareExecuteQuery(method, args, query.value(), query.order());

        }
        if (method.isAnnotationPresent(Update.class)) {
            Update update = method.getAnnotation(Update.class);
            return doPrepareExecuteUpdate(method, args, update.value(), update.order());

        }
        if (method.isAnnotationPresent(SqlPath.class)) {
            SqlPath sqlPath = method.getAnnotation(SqlPath.class);
            ExecuteMethod execType = sqlPath.method();
            String sql = new ClearNotesOnSqlHandler(sqlPath.value()).loadSql();

            if (execType == ExecuteMethod.SELECT) {
                return doPrepareExecuteQuery(method, args, sql, sqlPath.order());
            }
            if (execType == ExecuteMethod.UPDATE || execType == ExecuteMethod.DELETE || execType == ExecuteMethod.INSERT) {
                return doPrepareExecuteUpdate(method, args, sql, sqlPath.order());
            }
            return null;
        }
        throw new CustomCheckException(String.format("The '@Update' or '@Query' annotation was not found on the method : %s.%s()", targetClassName, method.getName()));
    }


    /**
    * 执行更新代理
    */
    private Object doPrepareExecuteUpdate(Method method, Object[] args, String sql, boolean isOrder) throws Exception {
        String methodName = String.format(" %s.%s() ", method.getDeclaringClass().getName(), method.getName());
        checkIllegalParam(methodName, isOrder, sql);

        if(sql.contains(SymbolConstant.PREPARE_BEGIN_REX_1) && isOrder) {
            throw new CustomCheckException(methodName + ExceptionConst.EX_USE_ORDER_FALSE);
        }
        if(sql.contains(SymbolConstant.QUEST) && !isOrder) {
            throw new CustomCheckException(methodName + ExceptionConst.EX_USE_ORDER_TRUE);
        }
        // 自定义-参数预编译
        ParameterParserExecutor parameterParserExecutor = new ParameterParserExecutor(sql, method, args);
        if(isOrder) {
            parameterParserExecutor.prepareOrderParams();
        }else {
            parameterParserExecutor.prepareDisorderParams();
        }
        List<Object> paramValues = parameterParserExecutor.getParamResList();
        return executeUpdate(parameterParserExecutor.getPrepareSql(), paramValues.toArray());
    }


    /**
    * 执行查询代理
    */
    private Object doPrepareExecuteQuery(Method method, Object[] args, String sql, boolean isOrder) throws Exception {
        String methodName = String.format(" %s.%s() ", method.getDeclaringClass().getName(), method.getName());
        checkIllegalParam(methodName, isOrder, sql);
        Type returnType = method.getGenericReturnType();
        // 自定义-参数预编译
        ParameterParserExecutor parameterParserExecutor = new ParameterParserExecutor(sql, method, args);
        if(isOrder) {
            parameterParserExecutor.prepareOrderParams();
        }else {
            parameterParserExecutor.prepareDisorderParams();
        }

        List<Object> paramValues = parameterParserExecutor.getParamResList();
        sql = parameterParserExecutor.getPrepareSql();
        Object[] params = paramValues.toArray();

        // 判断查询后的返回类型
        if (returnType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) returnType;
            Class<?> typeArgument = (Class<?>) pt.getActualTypeArguments()[0];
            Type type = ((ParameterizedType) returnType).getRawType();
            if (type.equals(List.class)) {
                return query(typeArgument, getDbCustomStrategy().isSqlOutPrinting(),  sql, params);

            } else if (type.equals(Map.class)) {
                return selectObjSql(Map.class, sql, params);

            } else if (type.equals(Set.class)) {
                return querySet(typeArgument, sql, params);
            }
        } else if (CustomUtil.isBasicType(returnType)) {
            return selectObjSql(sql, params);

        } else if (((Class<?>) returnType).isArray()) {
            Class<?> type = ((Class<?>) returnType).getComponentType();
            return queryArray(type, sql, method.getDeclaringClass().getName(), method.getName(), params);
        }else {
            String typeName = returnType.getTypeName();
            Class<?> cls = Class.forName(typeName);
            List<?> resultList = query(cls, getDbCustomStrategy().isSqlOutPrinting(), sql, params);
            int size = resultList.size();
            if(size == 0) {
                return null;
            }else if(size > SymbolConstant.DEFAULT_ONE) {
                throw new CustomCheckException(String.format(ExceptionConst.EX_QUERY_MORE_RESULT, size));
            }else {
                return resultList.get(SymbolConstant.DEFAULT_ZERO);
            }
        }
        return null;
    }

    /**
     * 检验参数合法性
     */
    private void checkIllegalParam(String methodName, boolean isOrder, String sql) {

        if(sql.contains(SymbolConstant.PREPARE_BEGIN_REX_1) && sql.contains(SymbolConstant.QUEST)) {
            log.error("如果isOrder为true，仅支持使用 \"?\"  如果isOrder为false 仅支持使用 \"#{ }\" 来设置参数");
            log.error("Error Method ==> {}", methodName);
            ExThrowsUtil.toCustom(String.format("The SQL cannot be resolved '%s'", sql));
        }
        if(isOrder && RexUtil.hasRegex(sql, RexUtil.sql_param)) {
            log.error("Error Method ==> {}", methodName);
            ExThrowsUtil.toCustom("方法注解上建议使用 isOrder = false");

        }else if(!isOrder && sql.contains(SymbolConstant.QUEST)) {
                log.error("Error Method ==> {}", methodName);
                ExThrowsUtil.toCustom("方法注解上建议使用 isOrder = true");
            }
    }


}
