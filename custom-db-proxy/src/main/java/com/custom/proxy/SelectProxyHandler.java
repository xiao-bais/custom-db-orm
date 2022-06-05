package com.custom.proxy;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtil;
import com.custom.comm.RexUtil;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.jdbc.ExecuteSqlHandler;
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
 * @desc:查询sql执行处理
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
    protected void prepareAndParamsParsing() {
        Parameter[] parameters = getMethod().getParameters();
        if (JudgeUtil.isEmpty(parameters)) {
            return;
        }
        for (int i = 0; i < parameters.length; i++) {
            Object prepareParam = getMethodParams()[i];
            Parameter parameter = parameters[i];
            String parameterName = parameter.getName();
            if (Objects.isNull(prepareParam)) {
                ExThrowsUtil.toNull(parameterName + " is null");
            }
            ParsingObjectStruts parsingObject = new ParsingObjectStruts();
            parsingObject.parser(parameterName, prepareParam);
            mergeParams(parsingObject.getParamsMap());

        }
    }

    @Override
    protected Object execute() throws Exception {
        String prepareSql = getPrepareSql();
        StringBuffer executeSql = new StringBuffer();
        ExecuteSqlHandler executeSqlHandler = getExecuteAction();

        if (RexUtil.hasRegex(prepareSql, RexUtil.sql_rep_param)) {
            handleRepSqlFormatParams(executeSql, prepareSql);
        }
        if (RexUtil.hasRegex(prepareSql, RexUtil.sql_set_param)) {
            executeSql = handleSetSqlFormatParams(JudgeUtil.isBlank(executeSql) ? prepareSql : executeSql.toString());
        }
        String readyExecuteSql = executeSql.toString();
        Object[] sqlParams = getExecuteSqlParams().toArray();

        Type returnType = getMethod().getGenericReturnType();
        Class<?> truthResType;
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
