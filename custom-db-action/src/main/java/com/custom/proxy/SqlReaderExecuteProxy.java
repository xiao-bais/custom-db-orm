package com.custom.proxy;

import com.custom.annotations.mapper.Query;
import com.custom.annotations.mapper.SqlPath;
import com.custom.annotations.mapper.Update;
import com.custom.comm.BasicDao;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.ExecuteMethod;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;
import com.custom.handler.DbParserFieldHandler;
import com.custom.dbaction.SqlExecuteAction;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.IntStream;

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
    private DbParserFieldHandler dbParserFieldHandler = new DbParserFieldHandler();;

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

        if (!BasicDao.class.isAssignableFrom(proxy.getClass())) {
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


    private Object doPrepareExecuteUpdate(Method method, Object[] args, String sql, boolean isOrder) throws Exception {

        String methodName = String.format(" %s.%s() ", method.getDeclaringClass().getName(), method.getName());

        if(sql.contains(SymbolConst.PREPARE_BEGIN_REX_1) && isOrder) {
            throw new CustomCheckException(methodName + ExceptionConst.EX_USE_ORDER_FALSE);
        }
        if(sql.contains(SymbolConst.QUEST) && !isOrder) {
            throw new CustomCheckException(methodName + ExceptionConst.EX_USE_ORDER_TRUE);
        }
        List<Object> paramValues = this.handleParamsPrepareSql(sql, method, args, isOrder);
        sql = String.valueOf(paramValues.get(paramValues.size() - 1));
        paramValues.remove(sql);
        return executeUpdate(sql, paramValues.toArray());
    }


    private Object doPrepareExecuteQuery(Method method, Object[] args, String sql, boolean isOrder) throws Exception {

        Type returnType = method.getGenericReturnType();
        List<Object> paramValues = this.handleParamsPrepareSql(sql, method, args, isOrder);
        sql = String.valueOf(paramValues.get(paramValues.size() - 1));
        paramValues.remove(sql);
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


    /**
     * 将参数中的占位符处理成‘?’
     */
    private List<Object> handleParamsPrepareSql(String sql, Method method, Object[] args, boolean isOrder) throws Exception {

        checkSqlByOrder(sql, isOrder);
        List<Object> paramRes = new ArrayList<>();
        Map<String, Object> paramsReplaces = new HashMap<>();
        String logSql = sql;

        Class<?>[] parameterTypes = method.getParameterTypes();
        if (isOrder) {

            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> type = parameterTypes[i];
                Object param = args[i];

                if (CustomUtil.isBasicType(type)) {
                    paramRes.add(param);
                    paramsReplaces.put(type.getName(), param);
                } else if (type.equals(List.class)) {
                    paramRes.addAll((List<Object>) param);
                } else if (type.equals(Arrays.class)) {
                    paramRes.addAll(Collections.singletonList(param));
                } else if (type.equals(Set.class)) {
                    Set<Object> paramsSet = (Set<Object>) param;
                    paramRes.addAll(paramsSet);
                } else
                    throw new IllegalArgumentException(String.format(ExceptionConst.EX_NOT_SUPPORT_MAP_OR_CUSTOM_ENTITY_PARAMS, method.getDeclaringClass().getName(), method.getName()));
            }
        } else {

            Map<Object, Object> paramsMap = new HashMap<>();
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameterTypes.length; i++) {
                Type type = parameterTypes[i];
                String paramName = parameters[i].getName();
                Object paramVal = args[i];

                if (CustomUtil.isBasicType(type)) {
                    paramsMap.put(paramName, paramVal);
                    paramsReplaces.put(paramName, paramVal);
                } else if (type.equals(Map.class)) {
                    paramsMap.putAll((Map<?, ?>) paramVal);
                    paramsReplaces.putAll((Map<String, ?>) paramVal);
                } else if(type.equals(List.class) || type.equals(Arrays.class) || type.equals(Set.class)) {
                    throw new IllegalArgumentException(String.format(ExceptionConst.EX_NOT_SUPPORT_ARRAY_PARAMS, method.getDeclaringClass().getName(), method.getName()));
                } else {
                    Field[] fields = CustomUtil.getFields(paramVal.getClass());
                    Object[] fieldNames = Arrays.stream(fields).map(Field::getName).toArray();
                    List<Object> fieldVales = dbParserFieldHandler.getFieldsVal(paramVal, Arrays.copyOf(fieldNames, fieldNames.length, String[].class));
                    IntStream.range(0, fieldNames.length).forEach(x -> paramsMap.put(fieldNames[x], fieldVales.get(x)));
                    IntStream.range(0, fieldNames.length).forEach(x -> paramsReplaces.put((String) fieldNames[x], fieldVales.get(x)));
                }
            }

            // 开始执行#{name} 替换为 ？
            int index = 0;
            while (true) {
                int[] indexes = CustomUtil.replaceSqlRex(sql, SymbolConst.PREPARE_BEGIN_REX_1, SymbolConst.PREPARE_END_REX, index);
                if (indexes == null) break;
                String text = sql.substring(indexes[0] + 2, indexes[1]);
                if(JudgeUtilsAx.isBlank(text)) throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_PARAMS_NAME, text, logSql));
                sql = sql.replace(sql.substring(indexes[0], indexes[1] + 1), SymbolConst.QUEST);
                index = indexes[2] - text.length() - 2;
                Object sqlParamsVal = paramsMap.get(text);
                if (JudgeUtilsAx.isEmpty(sqlParamsVal))
                    throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_PARAMS_VALUE, text, logSql));
                paramRes.add(sqlParamsVal);
            }
        }

        sql = this.replaceSqlSymbol(sql, paramsReplaces);
        paramRes.add(sql);
        return paramRes;
    }

    /**
    * 将${name} 替换为 `name`的值
    */
    private String replaceSqlSymbol(String sql, Map<String, Object> paramsMap) {

        if(!sql.contains(SymbolConst.PREPARE_BEGIN_REX_2)) {
            return sql;
        }
        int index = 0;
        while (true){
            int[] indexes = CustomUtil.replaceSqlRex(sql, SymbolConst.PREPARE_BEGIN_REX_2, SymbolConst.PREPARE_END_REX, index);
            if (indexes == null) break;
            String text = sql.substring(indexes[0] + 2, indexes[1]);
            Object param = paramsMap.get(text);
            if(JudgeUtilsAx.isEmpty(param)) {
                throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_PARAMS_NAME, text, sql));
            }
            sql = sql.replace(sql.substring(indexes[0], indexes[1] + 1), param.toString());
        }
        return sql;
    }

    //todo... 将规则判断迁移至此
    private void checkSqlByOrder(String sql, boolean isOrder){
        if (sql.contains(SymbolConst.QUEST) && sql.contains(SymbolConst.PREPARE_BEGIN_REX_1)) {
            log.error("无法解析：'#{ }' 与 '?' 不能作用在同一条sql上");
            throw new CustomCheckException(String.format(ExceptionConst.EX_UNABLE_TO_RESOLVE_SQL, sql));
        }


    }

}
