package com.custom.proxy;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtil;
import com.custom.comm.RexUtil;
import com.custom.comm.annotations.mapper.DbParam;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.jdbc.ExecuteSqlHandler;
import com.custom.proxy.AbstractProxyHandler;
import com.custom.proxy.ParsingObjectStruts;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Xiao-Bai
 * @date 2022/5/8 19:22
 * @desc: 代理：查询sql执行处理
 */
public class SelectProxyHandler extends AbstractProxyHandler {

    protected SelectProxyHandler(ExecuteSqlHandler executeSqlHandler, Object[] methodParams,
                                 String prepareSql, Method method) {

        super.setExecuteAction(executeSqlHandler);
        super.setMethodParams(methodParams);
        super.setPrepareSql(prepareSql);
        super.setMethod(method);
    }


    @Override
    protected Object execute() throws Exception {
        ExecuteSqlHandler executeSqlHandler = getExecuteAction();

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
                    return executeSqlHandler.query(genericType, true, readyExecuteSql, sqlParams);
                }else if (List.class.isAssignableFrom(truthResType)) {
                    return executeSqlHandler.querySet(genericType, readyExecuteSql, sqlParams);
                }
                // if not list or set, then throws error...
                ExThrowsUtil.toCustom("返回的列表类型暂时只支持List以及Set");
            }

            // do Map
            else if (Map.class.isAssignableFrom(truthResType)) {
                Class<?> genericType = (Class<?>) pt.getActualTypeArguments()[1];
                return executeSqlHandler.queryMap(genericType, readyExecuteSql, sqlParams);
            }
            else return null;
        }
        truthResType = getMethod().getReturnType();
        // do Array
        if (truthResType.isArray()) {
            return executeSqlHandler.queryArray(truthResType.getComponentType(), readyExecuteSql, sqlParams);
        }

        // do Basic type
        else if (CustomUtil.isBasicClass(truthResType)) {
            return executeSqlHandler.selectOneBasicBySql(readyExecuteSql, sqlParams);
        }

        // do custom Object
        return executeSqlHandler.selectObjSql(truthResType, readyExecuteSql, sqlParams);
    }


}
