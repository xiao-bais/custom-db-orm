package com.custom.proxy;

import com.custom.comm.JudgeUtilsAx;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.jdbc.ExecuteSqlHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
        if (JudgeUtilsAx.isEmpty(parameters)) {
            return;
        }
        for (int i = 0; i < parameters.length; i++) {
            Object prepareParam = getMethodParams()[i];
            Parameter parameter = parameters[i];
            String parameterName = parameter.getName();
            if (Objects.isNull(prepareParam)) {
                ExThrowsUtil.toNull(parameterName + " is null");
            }
            ParsingObjectStructs parsingObject = new ParsingObjectStructs();
            parsingObject.parser(parameterName, prepareParam);
            setParseAfterParams(parsingObject.getParamsMap());
        }
    }

    @Override
    protected Object execute() {


        return null;
    }
}
