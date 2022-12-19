package com.custom.action.extend;

import com.custom.action.core.HandleSelectSqlBuilder;
import com.custom.action.core.TableInfoCache;
import com.custom.action.core.TableParseModel;
import com.custom.action.dbaction.AbstractJoinToResult;
import com.custom.action.dbaction.AbstractSqlExecutor;
import com.custom.action.exceptions.QueryMultiException;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.comm.enums.MultiStrategy;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.utils.ReflectUtil;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Xiao-Bai
 * @date 2022/8/22 18:16
 * @desc 一对一，一对多 value注入
 */
public class MultiResultInjector<T> {

    private final Logger logger = LoggerFactory.getLogger(MultiResultInjector.class);

    /**
     * 主表的对象
     */
    private final Class<T> thisClass;
    private final Class<?> topNode;

    /**
     * select 查询对象
     */
    private final AbstractSqlExecutor sqlExecutor;
    private final JdbcExecutorFactory executorFactory;


    public MultiResultInjector(Class<T> thisClass, AbstractSqlExecutor sqlExecutor, Class<?> topNode) {
        this.thisClass = thisClass;
        this.sqlExecutor = sqlExecutor;
        this.executorFactory = sqlExecutor.getExecutorFactory();
        this.topNode = topNode;
    }


    public void injectorValue(List<T> resultList) throws Exception {
        TableParseModel<T> tableModel = TableInfoCache.getTableModel(thisClass);

        // set 一对一
        this.oneToOneHandler(tableModel, resultList);

        // set 一对多
        this.oneToManyHandler(tableModel, resultList);

    }

    private final static Map<String, DbJoinToManyParseModel> MANY_MODEL_CACHE = new ConcurrentHashMap<>();

    /**
     * 一对多的值set
     * @param tableModel - 实体解析模板
     * @param entityList - 实体对象
     */
    private void oneToManyHandler(TableParseModel<T> tableModel, List<T> entityList) throws Exception {
        List<Field> oneToManyFieldList = tableModel.getOneToManyFieldList();
        if (JudgeUtil.isNotEmpty(oneToManyFieldList)) {
            for (Field waitSetField : oneToManyFieldList) {
//                System.out.println("waitSetField = " + waitSetField);

                String key = waitSetField.getDeclaringClass().getName() + "." + waitSetField.getName();
                DbJoinToManyParseModel joinToManyParseModel = MANY_MODEL_CACHE.get(key);
                if (joinToManyParseModel == null) {
                    joinToManyParseModel = new DbJoinToManyParseModel(waitSetField, topNode);
                    MANY_MODEL_CACHE.putIfAbsent(key, joinToManyParseModel);
                }
                Class<?> joinTarget = joinToManyParseModel.getJoinTarget();

                HandleSelectSqlBuilder<?> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(joinTarget, executorFactory);
                String condPrefix = joinToManyParseModel.queryCondPrefix();
                String condSuffix = joinToManyParseModel.queryCondSuffix();

                for (T entity : entityList) {
                    if (entity != null) {
                        List<?> queryResult = this.queryResult(entity, joinToManyParseModel, condPrefix,
                                condSuffix, joinTarget, sqlBuilder);
                        if (JudgeUtil.isNotEmpty(queryResult)) {
                            ReflectUtil.writeFieldValue(queryResult, entity,
                                    waitSetField.getName(), joinToManyParseModel.getJoinTarget());
                        }
                    }
                }
            }
        }
    }



    private final static Map<String, DbJoinToOneParseModel> ONE_MODEL_CACHE = new ConcurrentHashMap<>();
    /**
     * 一对一的值set
     * @param tableModel 实体解析模板
     * @param entityList 实体对象集合
     */
    private void oneToOneHandler(TableParseModel<T> tableModel, List<T> entityList) throws Exception {
        List<Field> oneToOneFieldList = tableModel.getOneToOneFieldList();
        if (JudgeUtil.isEmpty(oneToOneFieldList)) {
            return;
        }
        for (Field waitSetField : oneToOneFieldList) {

            // todo 若使用缓存，则会变成死循环，但是又需要类的缓存，保证下一次循环时能够不做重复的检查
            // todo 办法一，生成一个秘钥，用于
            String key = waitSetField.getDeclaringClass().getName() + "." + waitSetField.getName();
            DbJoinToOneParseModel joinToOneParseModel = ONE_MODEL_CACHE.get(key);
            if (joinToOneParseModel == null) {
                joinToOneParseModel = new DbJoinToOneParseModel(waitSetField, topNode);
                ONE_MODEL_CACHE.putIfAbsent(key, joinToOneParseModel);
            }
            Class<?> joinTarget = joinToOneParseModel.getJoinTarget();
            HandleSelectSqlBuilder<?> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(joinTarget, executorFactory);
            String condPrefix = joinToOneParseModel.queryCondPrefix();
            String condSuffix = joinToOneParseModel.queryCondSuffix();

            for (T entity : entityList) {
                if (entity != null) {
                    List<?> queryResult = this.queryResult(entity, joinToOneParseModel, condPrefix,
                            condSuffix, joinTarget, sqlBuilder);
                    if (JudgeUtil.isNotEmpty(queryResult)) {
                        if (queryResult.get(0) == null) {
                            continue;
                        } else if (queryResult.size() > 1 && joinToOneParseModel.isThrowErr()) {
                            throw new CustomCheckException(joinToOneParseModel.getJoinTarget()
                                    + "One to one query, but %s results are found", queryResult.size());
                        }
                        ReflectUtil.writeFieldValue(
                                queryResult.get(0),
                                entity,
                                waitSetField.getName(),
                                joinToOneParseModel.getJoinTarget()
                        );
                    }
                }


            }
        }
    }


    /**
     * 查询结果
     * @param entity 当前待注入的实体对象
     * @param thisNodeResult 关联的解析类
     * @param condPrefix 查询的前置条件(where之后， group by 之前)
     * @param condSuffix 查询的后置条件(group by之后)
     * @param joinTarget 查询的对象
     * @param sqlBuilder 查询的sql创建对象
     * @return Lists
     * @throws Exception
     */
    private List<?> queryResult(T entity, AbstractJoinToResult thisNodeResult, String condPrefix,
                                String condSuffix, Class<?> joinTarget, HandleSelectSqlBuilder<?> sqlBuilder) throws Exception {
        Object queryValue = null;
        try {
            queryValue = ReflectUtil.readFieldValue(entity, thisNodeResult.getThisField());
            if (queryValue == null) {
                return null;
            }

        } catch (NoSuchFieldException e) {
            logger.error(e.getMessage(), e);
        }

        // 若该表存在逻辑删除的字段，则处理逻辑删除条件
        FullSqlConditionExecutor conditionExecutor = sqlBuilder.addLogicCondition(condPrefix);
        String selectSql = sqlBuilder.createTargetSql() + conditionExecutor.execute() + condSuffix;

        if (thisNodeResult.getStrategy() == MultiStrategy.NONE) {
            return executorFactory.selectListBySql(joinTarget, selectSql, queryValue);
        } else {
            try {
                return sqlExecutor.selectListBySqlAndInject(joinTarget, topNode, selectSql, queryValue);
            } catch (Exception e) {
                QueryMultiException childQme = null;
                if (e instanceof QueryMultiException) {
                    childQme = (QueryMultiException) e;
                } else if (e.getCause() instanceof QueryMultiException) {
                    childQme = (QueryMultiException) e.getCause();
                }
                if (childQme != null) {
                    // 若子节点是抛错，且父节点也是抛错，则往外抛出
                    if (childQme.getStrategy() == MultiStrategy.ERROR) {
                        if (thisNodeResult.getStrategy() == MultiStrategy.ERROR) {
                            throw e;
                        }else {
                            logger.warn(e.getMessage());
                            return null;
                        }
                    }
                    return null;
                }
                throw e;
            }
        }
    }


}
