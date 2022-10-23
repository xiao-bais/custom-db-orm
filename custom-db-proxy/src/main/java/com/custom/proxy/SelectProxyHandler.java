package com.custom.proxy;

import com.custom.comm.utils.CustomUtil;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.jdbc.condition.SelectExecutorModel;
import com.custom.jdbc.select.CustomSelectJdbcBasic;
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

    protected SelectProxyHandler(CustomSelectJdbcBasic selectJdbc, Object[] methodParams,
                                 String prepareSql, Method method) {

        super.setSelectJdbc(selectJdbc);
        super.setMethodParams(methodParams);
        super.setPrepareSql(prepareSql);
        super.setMethod(method);
    }


    @Override
    protected Object execute() throws Exception {
        CustomSelectJdbcBasic selectJdbc = getSelectJdbc();
        SelectExecutorModel<?> sqlParamInfo;
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
                sqlParamInfo = new SelectExecutorModel<>(genericType, readyExecuteSql, sqlParams);

                if (List.class.isAssignableFrom(truthResType)) {
                    return selectJdbc.selectList(sqlParamInfo);

                }else if (List.class.isAssignableFrom(truthResType)) {
                    return selectJdbc.selectSet(sqlParamInfo);
                }
                // if not list or set, then throws error...
                ExThrowsUtil.toCustom("返回的列表类型暂时只支持List以及Set");
            }

            // do Map
            else if (Map.class.isAssignableFrom(truthResType)) {
                genericType = (Class<?>) pt.getActualTypeArguments()[1];
                sqlParamInfo = new SelectExecutorModel<>(genericType, readyExecuteSql, sqlParams);
                return selectJdbc.selectMap(sqlParamInfo);
            }
            else return null;
        }
        truthResType = getMethod().getReturnType();
        // do Array
        if (truthResType.isArray()) {
            sqlParamInfo = new SelectExecutorModel<>(truthResType.getComponentType(), readyExecuteSql, sqlParams);
            return selectJdbc.selectArrays(sqlParamInfo);
        }

        // do Basic type
        else if (CustomUtil.isBasicClass(truthResType)) {
            SelectExecutorModel<Object> objSqlParam = new SelectExecutorModel<>(Object.class, readyExecuteSql, sqlParams);
            return selectJdbc.selectObj(objSqlParam);
        }

        // do custom Object
        sqlParamInfo = new SelectExecutorModel<>(truthResType, readyExecuteSql, sqlParams);
        return selectJdbc.selectOne(sqlParamInfo);
//        return jdbcExecutor.selectBasicObjBySql(readyExecuteSql, sqlParams);
    }


}
