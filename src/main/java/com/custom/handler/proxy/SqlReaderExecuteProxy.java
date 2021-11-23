package com.custom.handler.proxy;

import com.custom.annotations.reader.Query;
import com.custom.annotations.reader.Update;
import com.custom.comm.BasicDao;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.RegisterBeanExecutor;
import com.custom.dbconfig.SymbolConst;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;
import com.custom.handler.DbParserFieldHandler;
import com.custom.handler.SqlExecuteHandler;
import com.custom.scanner.MapperBeanScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.IntStream;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/19 16:45
 * @Desc：用于读取接口上的注解，并生成代理类去执行路径中文件中的内容
 **/
@SuppressWarnings("unchecked")
@Slf4j
public class SqlReaderExecuteProxy extends SqlExecuteHandler implements InvocationHandler {


    @Resource(name = "registerBeanExecutor")
    private ApplicationContextAware registerBeanExecutor;

    public <T> T createProxy(Class<T> cls) {
        ClassLoader classLoader = cls.getClassLoader();
        Class<?>[] interfaces = new Class[]{cls};
        return (T) Proxy.newProxyInstance(classLoader, interfaces, this);
    }


    public SqlReaderExecuteProxy(DbDataSource dbDataSource) {
        super(dbDataSource, new DbParserFieldHandler());
        if(getDbCustomStrategy().isMapperScanEnable()) {
            this.registerBean();
        }
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {

        if(!BasicDao.class.isAssignableFrom(proxy.getClass())) {
            throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_INHERITED_BASIC_DAO, method.getDeclaringClass()));
        }

        try {
            if (method.isAnnotationPresent(Query.class)) {
                return doQueryInvoke(method, args);
            } else if (method.isAnnotationPresent(Update.class)) {
                return doUpdateInvoke(method, args);
            }
        } catch (Exception e) {
            if (e instanceof CustomCheckException) {
                throw e;
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
        List<Object> paramValues = this.handleParamsPrepareSql(sql, method, args, query.isOrder());
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
            return queryArray(type, sql,method.getDeclaringClass().getName(), method.getName(), params);
        }
        String typeName = returnType.getTypeName();
        Class<?> cls = Class.forName(typeName);
        return selectOneSql(cls, sql, params);
    }


    /**
     * 将参数中的占位符处理成‘?’
     */
    private List<Object> handleParamsPrepareSql(String sql, Method method, Object[] args, boolean isOrder) throws Exception {

        List<Object> paramRes = new ArrayList<>();
        String logSql = sql;

        Class<?>[] parameterTypes = method.getParameterTypes();
        if(isOrder) {

            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> type = parameterTypes[i];
                Object param = args[i];

                if (CustomUtil.isBasicType(type)) {
                    paramRes.add(param);
                } else if (type.equals(List.class)) {
                    paramRes.addAll((List<Object>) param);
                } else if (type.equals(Arrays.class)) {
                    paramRes.addAll(Collections.singletonList(param));
                }else if(type.equals(Set.class)) {
                    Set<Object> paramsSet = (Set<Object>) param;
                    paramRes.addAll(paramsSet);
                }else throw new IllegalArgumentException(String.format(ExceptionConst.EX_NOT_SUPPORT_MAP_OR_CUSTOM_ENTITY_PARAMS, method.getDeclaringClass().getName(), method.getName()));
            }
        }else {

            Map<Object, Object> paramsMap = new HashMap<>();
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> type = parameterTypes[i];
                String paramName = parameters[i].getName();
                Object paramVal = args[i];

                if(CustomUtil.isBasicType(type)) {
                    paramsMap.put(paramName, paramVal);
                }else if(type.equals(Map.class)){
                    paramsMap.putAll((Map<?, ?>) paramVal);
                }else {
                    Field[] fields = CustomUtil.getFields(paramVal.getClass());
                    Object[] fieldNames = Arrays.stream(fields).map(Field::getName).toArray();
                    List<Object> fieldVales = getParserFieldHandler().getFieldsVal(paramVal.getClass(), (String[]) fieldNames);
                    IntStream.range(0, fieldNames.length).forEach(x -> paramsMap.put(fieldNames[x], fieldVales.get(x)));
                }
            }

            int index = 0;
            while (true){
                int[] indexes = CustomUtil.replaceSqlRex(sql, SymbolConst.PREPARE_BEGIN_REX, SymbolConst.PREPARE_END_REX, index);
                if(indexes == null) break;
                String text = sql.substring(indexes[0] + 2, indexes[1]);
                sql = sql.replace(sql.substring(indexes[0], indexes[1] + 1), SymbolConst.QUEST);
                index = indexes[2];
                Object sqlParamsVal = paramsMap.get(text);
                if(JudgeUtilsAx.isEmpty(sqlParamsVal)) throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_PARAMS_VALUE, text, logSql));
                paramRes.add(sqlParamsVal);
            }
        }
        paramRes.add(sql);
        return paramRes;
    }


    private void registerBean() {
        DbCustomStrategy dbCustomStrategy = this.getDbCustomStrategy();
        String[] packageScans = dbCustomStrategy.getPackageScans();
        if(JudgeUtilsAx.isEmpty(packageScans)) {
            log.error("需要设置扫描包地址");
            throw new NullPointerException();
        }

        creab(packageScans);



    }

    @Bean
    public List<Class<? extends String>> creab(String[] packageScans) {
        MapperBeanScanner mapperBeanScanner = new MapperBeanScanner();
        return mapperBeanScanner.getBeanRegisterList();
    }

}
