package com.custom.proxy;

import com.custom.jdbc.ExecuteSqlHandler;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Xiao-Bai
 * @date 2022/5/8 19:30
 * @desc:
 */
public abstract class AbstractProxyHandler {

    /**
     * jdbc执行对象
     */
    private ExecuteSqlHandler executeAction;
    /**
     * 方法参数
     */
    private Object[] methodParams;
    /**
     * 原生sql
     */
    private String prepareSql;
    /**
     * 当前执行方法
     */
    private Method method;
    /**
     * 解析后参数键值对
     */
    private Map<String, Object> parseAfterParams;

    private final Map<String, Object> structureMap = new HashMap<>();

    /**
     * 参数解析+sql预编译
     */
    protected abstract void prepareAndParamsParsing();
    /**
     * 执行
     */
    protected abstract Object execute();

    protected ExecuteSqlHandler getExecuteAction() {
        return executeAction;
    }

    protected void setExecuteAction(ExecuteSqlHandler executeAction) {
        this.executeAction = executeAction;
    }

    protected Object[] getMethodParams() {
        return methodParams;
    }

    protected void setMethodParams(Object[] methodParams) {
        this.methodParams = methodParams;
    }

    public Map<String, Object> getParseAfterParams() {
        return parseAfterParams;
    }

    public void setParseAfterParams(Map<String, Object> parseAfterParams) {
        this.parseAfterParams = parseAfterParams;
    }

    protected String getPrepareSql() {
        return prepareSql;
    }

    protected void setPrepareSql(String prepareSql) {
        this.prepareSql = prepareSql;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
