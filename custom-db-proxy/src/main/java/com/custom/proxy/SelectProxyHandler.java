package com.custom.proxy;

import com.custom.comm.CustomUtil;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.jdbc.JdbcExecutorImpl;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Xiao-Bai
 * @date 2022/5/8 19:22
 * @desc: 代理：查询sql执行处理
 */
public class SelectProxyHandler extends AbstractProxyHandler {

    protected SelectProxyHandler(JdbcExecutorImpl jdbcExecutor, Object[] methodParams,
                                 String prepareSql, Method method) {

        super.setExecuteAction(jdbcExecutor);
        super.setMethodParams(methodParams);
        super.setPrepareSql(prepareSql);
        super.setMethod(method);
    }


    @Override
    protected Object execute() throws Exception {
        JdbcExecutorImpl jdbcExecutor = getExecuteAction();

        String readyExecuteSql = sqlExecuteParamParser();
        Object[] sqlParams = getExecuteSqlParams().toArray();

        Type returnType = getMethod().getGenericReturnType();
        Class<?> truthResType;
        // 若返回值是带有泛型的类型
        if (returnType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) returnType;
            truthResType = ((ParameterizedTypeImpl) pt).getRawType();
            // do Collection
            if (Collection.class.isAssignableFrom(truthResType)) {
                Class<?> genericType = (Class<?>) pt.getActualTypeArguments()[0];
                if (List.class.isAssignableFrom(truthResType)) {
                    return jdbcExecutor.selectList(genericType, true, readyExecuteSql, sqlParams);
                }else if (List.class.isAssignableFrom(truthResType)) {
                    return jdbcExecutor.selectSet(genericType, readyExecuteSql, sqlParams);
                }
                // if not list or set, then throws error...
                ExThrowsUtil.toCustom("返回的列表类型暂时只支持List以及Set");
            }

            // do Map
            else if (Map.class.isAssignableFrom(truthResType)) {
                Class<?> genericType = (Class<?>) pt.getActualTypeArguments()[1];
                return jdbcExecutor.selectMap(genericType, readyExecuteSql, sqlParams);
            }
            else return null;
        }
        truthResType = getMethod().getReturnType();
        // do Array
        if (truthResType.isArray()) {
            return jdbcExecutor.selectArray(truthResType.getComponentType(), readyExecuteSql, sqlParams);
        }

        // do Basic type
        else if (CustomUtil.isBasicClass(truthResType)) {
            return jdbcExecutor.selectBasicObjBySql(readyExecuteSql, sqlParams);
        }

        // do custom Object
        return jdbcExecutor.selectGenericObjSql(truthResType, readyExecuteSql, sqlParams);
    }


}
