package com.custom.proxy;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtil;
import com.custom.comm.RexUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.jdbc.ExecuteSqlHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Xiao-Bai
 * @date 2022/5/8 19:30
 * @desc:
 */
public abstract class AbstractProxyHandler {

    private static Logger logger = LoggerFactory.getLogger(AbstractProxyHandler.class);

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

    /**
     * 执行sql的参数
     */
    private final List<Object> executeSqlParams = new ArrayList<>();

    /**
     * 空字符串
     */
    protected final static String nullStr = "null";

    /**
     * 参数解析+sql预编译
     */
    protected abstract void prepareAndParamsParsing();
    /**
     * 执行
     */
    protected abstract Object execute() throws Exception;

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

    public void mergeParams(Map<String, Object> parseAfterParams) {
        if (this.parseAfterParams == null) {
            this.parseAfterParams = new HashMap<>();
        }
        if (parseAfterParams != null) {
            this.parseAfterParams.putAll(parseAfterParams);
        }
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

    public List<Object> getExecuteSqlParams() {
        return executeSqlParams;
    }

    /**
     * 第一步：处理可替换的sql参数
     * @param executeSql 预编译执行的sql
     * @param prepareSql 用户原生编写的sql
     */
    protected void handleRepSqlFormatParams(StringBuffer executeSql, String prepareSql) {
        Pattern pattern = Pattern.compile(RexUtil.sql_rep_param);
        Matcher matcher = pattern.matcher(prepareSql);
        while (matcher.find()) {
            String repExParam = matcher.group(1);
            Optional<Object> findSqlParam = this.parseAfterParams.entrySet().stream()
                    .filter(x -> x.getKey().equals(repExParam))
                    .map(Map.Entry::getValue)
                    .findFirst();
            findSqlParam.ifPresent(o -> matcher.appendReplacement(executeSql, JudgeUtil.isEmpty(o) ? nullStr : String.valueOf(o)));
        }
        matcher.appendTail(executeSql);
    }

    /**
     * 第二步：处理预编译的sql参数
     * @param prepareSql 用户原生编写的sql(完成第一步后的sql)
     */
    protected StringBuffer handleSetSqlFormatParams(String prepareSql) {
        Pattern pattern = Pattern.compile(RexUtil.sql_set_param);
        Matcher matcher = pattern.matcher(prepareSql);
        StringBuffer exBuffer = new StringBuffer();
        while (matcher.find()) {
            String setExParam = matcher.group(1);
            Optional<Map.Entry<String, Object>> findSqlParam = this.parseAfterParams.entrySet().stream()
                    .filter(x -> x.getKey().equals(setExParam))
                    .findFirst();
            findSqlParam.ifPresent(o -> {
                Object value = o.getValue();
                if (Objects.isNull(value)) {
                    ExThrowsUtil.toNull(String.format("%s is null", o.getKey()));
                }

                if (CustomUtil.isBasicType(value)) {
                    matcher.appendReplacement(exBuffer, SymbolConstant.QUEST);
                    this.executeSqlParams.add(value);
                }else if (value instanceof Collection) {
                    StringJoiner symbolQuest = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
                    Collection<?> paramCollection = (Collection<?>) value;
                    for (Object item : paramCollection) {
                        this.executeSqlParams.add(item);
                        symbolQuest.add(SymbolConstant.QUEST);
                    }
                    matcher.appendReplacement(exBuffer, symbolQuest.toString());
                }
                // else ignore...
            });
        }
        matcher.appendTail(exBuffer);
        return exBuffer;
    }
}
