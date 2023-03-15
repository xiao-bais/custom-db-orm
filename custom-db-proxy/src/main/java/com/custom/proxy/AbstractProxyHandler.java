package com.custom.proxy;

import com.custom.comm.annotations.mapper.DbParam;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.utils.RexUtil;
import com.custom.jdbc.executebody.SaveExecutorBody;
import com.custom.jdbc.executebody.SelectExecutorBody;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author  Xiao-Bai
 * @since  2022/5/8 19:30
 * @desc 抽象的sql代理执行层
 * 1.prepareParamsParsing：参与sql语句的参数解析以及部分sql的替换操作
 * 2.execute：负责执行解析后的sql，以及解析参数的返回类型
 */
public abstract class AbstractProxyHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractProxyHandler.class);

    /**
     * jdbc执行对象
     */
    private JdbcExecutorFactory executorFactory;

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
     * 执行
     */
    protected abstract Object execute() throws Exception;

    public void setExecutorFactory(JdbcExecutorFactory executorFactory) {
        this.executorFactory = executorFactory;
    }

    protected Object[] getMethodParams() {
        return methodParams;
    }

    protected void setMethodParams(Object[] methodParams) {
        this.methodParams = methodParams;
    }

    public void mergeParams(Map<String, Object> parseAfterParams) {
        if (this.parseAfterParams == null) {
            this.parseAfterParams = new HashMap<>();
        }
        if (parseAfterParams != null) {
            this.parseAfterParams.putAll(parseAfterParams);
        }
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

    public JdbcExecutorFactory thisJdbcExecutor() {
        return executorFactory;
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
            if (!findSqlParam.isPresent()) {
                logger.error("\nSQL ERROR ==>\n{}\n", prepareSql);
                throw new CustomCheckException(String.format("Parameter '%s' not found", setExParam));
            }
            Map.Entry<String, Object> entryParam = findSqlParam.get();
            Object value = entryParam.getValue();
            if (Objects.isNull(value)) {
                logger.error("\nSQL ERROR ==>\n{}\n", prepareSql);
                throw new NullPointerException(String.format("Parameter %s is null", entryParam.getKey()));
            }

            if (CustomUtil.isBasicType(value)) {
                matcher.appendReplacement(exBuffer, Constants.QUEST);
                this.executeSqlParams.add(value);
            }else if (value instanceof Collection) {
                StringJoiner symbolQuest = new StringJoiner(Constants.SEPARATOR_COMMA_2);
                Collection<?> paramCollection = (Collection<?>) value;
                for (Object item : paramCollection) {
                    this.executeSqlParams.add(item);
                    symbolQuest.add(Constants.QUEST);
                }
                matcher.appendReplacement(exBuffer, symbolQuest.toString());
            }
            // else ignore...
        }
        matcher.appendTail(exBuffer);
        return exBuffer;
    }

    /**
     * sql执行参数解析
     */
    protected String sqlExecuteParamParser() {
        StringBuffer executeSql = new StringBuffer();

        // step 1
        if (RexUtil.hasRegex(prepareSql, RexUtil.sql_rep_param)) {
            handleRepSqlFormatParams(executeSql, prepareSql);
        }
        // step 2
        if (RexUtil.hasRegex(prepareSql, RexUtil.sql_set_param)) {
            executeSql = handleSetSqlFormatParams(JudgeUtil.isBlank(executeSql) ? prepareSql : executeSql.toString());
        }
        return executeSql.toString();
    }

    /**
     * 参数解析+sql预编译
     */
    protected void prepareParamsParsing() {
        Parameter[] parameters = getMethod().getParameters();
        if (JudgeUtil.isEmpty(parameters)) {
            return;
        }
        for (int i = 0; i < parameters.length; i++) {
            Object prepareParam = getMethodParams()[i];
            Parameter parameter = parameters[i];
            String parameterName = parameter.getName();

            if (parameter.isAnnotationPresent(DbParam.class)) {
                DbParam dbParam = parameter.getAnnotation(DbParam.class);
                if (JudgeUtil.isNotEmpty(dbParam.value())) {
                    parameterName = dbParam.value();
                }
            }

            if (Objects.isNull(prepareParam)) {
                throw new NullPointerException(parameterName + " is null");
            }
            ParsingObjectStruts parsingObject = new ParsingObjectStruts();
            parsingObject.parser(parameterName, prepareParam);
            mergeParams(parsingObject.getParamsMap());
        }
    }

    public <V> Map<String, V> selectMapBySql(Class<V> t, String sql, Object... params) throws Exception {
        SelectExecutorBody<V> paramInfo = new SelectExecutorBody<>(t, sql, params);
        CustomSqlSession sqlSession = executorFactory.createSqlSession(paramInfo);
        return executorFactory.getJdbcExecutor().selectOneMap(sqlSession);
    }

    /**
     * 查询单列的Set集合
     */
    public <T> Set<T> selectSetBySql(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorBody<T> paramInfo = new SelectExecutorBody<>(t, sql, params);
        CustomSqlSession sqlSession = executorFactory.createSqlSession(paramInfo);
        return executorFactory.getJdbcExecutor().selectSet(sqlSession);
    }

    /**
     * 纯sql查询单条记录
     */
    public <T> T selectOneSql(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorBody<T> paramInfo = new SelectExecutorBody<>(t, sql, params);
        CustomSqlSession sqlSession = executorFactory.createSqlSession(paramInfo);
        return executorFactory.getJdbcExecutor().selectOne(sqlSession);
    }

    /**
     * 纯sql查询集合
     */
    public <T> List<T> selectListBySql(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorBody<T> paramInfo = new SelectExecutorBody<>(t, sql, params);
        CustomSqlSession sqlSession = executorFactory.createSqlSession(paramInfo);
        return executorFactory.getJdbcExecutor().selectList(sqlSession);
    }

    /**
     * 查询数组
     */
    public <T> T[] selectArrays(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorBody<T> paramInfo = new SelectExecutorBody<>(t, sql, params);
        CustomSqlSession sqlSession = executorFactory.createSqlSession(paramInfo);
        return executorFactory.getJdbcExecutor().selectArrays(sqlSession);
    }

    public Object selectObjBySql(String sql, Object... params) throws Exception {
        SelectExecutorBody<Object> paramInfo = new SelectExecutorBody<>(Object.class, sql, params);
        CustomSqlSession sqlSession = executorFactory.createSqlSession(paramInfo);
        return executorFactory.getJdbcExecutor().selectObj(sqlSession);
    }

    public Object executeAnySql(String readyExecuteSql, Object[] sqlParams) throws Exception {
        SaveExecutorBody<Object> executorBody = new SaveExecutorBody<>(readyExecuteSql, sqlParams);
        CustomSqlSession sqlSession = executorFactory.createSqlSession(executorBody);
        return executorFactory.getJdbcExecutor().executeUpdate(sqlSession);
    }
}
