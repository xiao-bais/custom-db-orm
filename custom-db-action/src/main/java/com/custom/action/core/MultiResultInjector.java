package com.custom.action.core;

import com.custom.action.dbaction.AbstractSqlExecutor;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.utils.ReflectUtil;
import com.custom.jdbc.configuration.DbDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

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

    /**
     * select 查询对象
     */
    private final AbstractSqlExecutor sqlExecutor;

    public MultiResultInjector(Class<T> thisClass, AbstractSqlExecutor sqlExecutor) {
        this.thisClass = thisClass;
        this.sqlExecutor = sqlExecutor;
    }


    public void injectorValue(List<T> resultList) throws Exception {
        TableParseModel<T> tableModel = TableInfoCache.getTableModel(thisClass);

        // set 一对一
        this.oneToOneHandler(tableModel, resultList);

        // set 一对多
        this.oneToManyHandler(tableModel, resultList);

    }

    /**
     * 一对多的值set
     * @param tableModel - 实体解析模板
     * @param entityList - 实体对象
     */
    private void oneToManyHandler(TableParseModel<T> tableModel, List<T> entityList) throws Exception {
        List<Field> oneToManyFieldList = tableModel.getOneToManyFieldList();
        if (JudgeUtil.isNotEmpty(oneToManyFieldList)) {
            for (Field waitSetField : oneToManyFieldList) {

                DbJoinToManyParseModel joinToManyParseModel = new DbJoinToManyParseModel(waitSetField);
                Class<?> joinTarget = joinToManyParseModel.getJoinTarget();

                HandleSelectSqlBuilder<?> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(joinTarget, sqlExecutor.getExecutorFactory());
                String condPrefix = joinToManyParseModel.queryCondPrefix();
                String condSuffix = joinToManyParseModel.queryCondSuffix();

                for (T entity : entityList) {
                    if (entity != null) {
                        List<?> queryResult = this.queryResult(entity, joinToManyParseModel.getThisField(), condPrefix,
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
            DbJoinToOneParseModel joinToOneParseModel = new DbJoinToOneParseModel(waitSetField);
            Class<?> joinTarget = joinToOneParseModel.getJoinTarget();
            HandleSelectSqlBuilder<?> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(joinTarget, sqlExecutor.getExecutorFactory());
            String condPrefix = joinToOneParseModel.queryCondPrefix();
            String condSuffix = joinToOneParseModel.queryCondSuffix();

            for (T entity : entityList) {
                if (entity != null) {
                    List<?> queryResult = this.queryResult(entity, joinToOneParseModel.getThisField(), condPrefix,
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
     * @param thisField 查询的关联java字段
     * @param condPrefix 查询的前置条件(where之后， group by 之前)
     * @param condSuffix 查询的后置条件(group by之后)
     * @param joinTarget 查询的对象
     * @param sqlBuilder 查询的sql创建对象
     * @return Lists
     * @throws Exception
     */
    private List<?> queryResult(T entity, String thisField, String condPrefix,
                                String condSuffix, Class<?> joinTarget, HandleSelectSqlBuilder<?> sqlBuilder) throws Exception {
        try {
            Object queryValue = ReflectUtil.readFieldValue(entity, thisField);
            if (queryValue == null) {
                return null;
            }

            // 若该表存在逻辑删除的字段，则处理逻辑删除条件
            FullSqlConditionExecutor conditionExecutor = sqlBuilder.addLogicCondition(condPrefix);

            String selectSql = sqlBuilder.createTargetSql() + conditionExecutor.execute() + condSuffix;
            return sqlExecutor.selectListBySql(joinTarget, selectSql, queryValue);

        }catch (NoSuchFieldException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


}
