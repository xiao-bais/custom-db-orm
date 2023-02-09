package com.custom.proxy;

import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.CustomUtil;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author  Xiao-Bai
 * @since  2022/5/8 19:22
 * @desc: 代理：查询sql执行处理
 */
public class SelectProxyHandler extends AbstractProxyHandler {

    protected SelectProxyHandler(JdbcExecutorFactory executorFactory, Object[] methodParams,
                                 String prepareSql, Method method) {

        super.setExecutorFactory(executorFactory);
        super.setMethodParams(methodParams);
        super.setPrepareSql(prepareSql);
        super.setMethod(method);
    }


    @Override
    protected Object execute() throws Exception {
        JdbcExecutorFactory executorFactory = thisJdbcExecutor();
        String readyExecuteSql = sqlExecuteParamParser();
        Object[] sqlParams = getExecuteSqlParams().toArray();

        Type returnType = getMethod().getGenericReturnType();
        // 真实返回类型
        Class<?> truthResType;
        // 返回类型中的泛型类型
        Class<?> genericType;
        // 若返回值是带有泛型的类型
        if (returnType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) returnType;
            truthResType = ((ParameterizedTypeImpl) pt).getRawType();
            // do Collection
            if (Collection.class.isAssignableFrom(truthResType)) {
                genericType = (Class<?>) pt.getActualTypeArguments()[0];

                if (List.class.isAssignableFrom(truthResType)) {
                    return executorFactory.selectListBySql(genericType, readyExecuteSql, sqlParams);

                }else if (Set.class.isAssignableFrom(truthResType)) {
                    return executorFactory.selectSetBySql(genericType, readyExecuteSql, sqlParams);
                }
                // if not list or set, then throws error...
                throw new CustomCheckException("返回的列表类型暂时只支持List以及Set");
            }

            // do Map
            else if (Map.class.isAssignableFrom(truthResType)) {
                genericType = (Class<?>) pt.getActualTypeArguments()[1];
                return executorFactory.selectMapBySql(genericType, readyExecuteSql, sqlParams);
            }
            else return null;
        }
        truthResType = getMethod().getReturnType();
        // do Array
        if (truthResType.isArray()) {
            genericType = truthResType.getComponentType();
            return executorFactory.selectArrays(genericType, readyExecuteSql, sqlParams);
        }

        // do Basic type
        else if (CustomUtil.isBasicClass(truthResType)) {
            return executorFactory.selectObjBySql(readyExecuteSql, sqlParams);
        }

        // do custom Object
        return executorFactory.selectOneSql(truthResType, readyExecuteSql, sqlParams);
    }


}
