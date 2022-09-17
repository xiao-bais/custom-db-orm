package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.dbaction.AbstractSqlExecutor;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtil;
import com.custom.comm.exceptions.ExThrowsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Xiao-Bai
 * @date 2022/8/22 18:16
 * @desc 一对一，一对多 value注入
 */
public class MappingResultInjector<T> {

    private final Logger logger = LoggerFactory.getLogger(MappingResultInjector.class);

    /**
     * 主表的对象
     */
    private final Class<T> thisClass;

    /**
     * select 查询对象
     */
    public AbstractSqlExecutor sqlExecutor;

    public MappingResultInjector(Class<T> thisClass, AbstractSqlExecutor sqlExecutor) {
        this.thisClass = thisClass;
        this.sqlExecutor = sqlExecutor;
    }


    public void injectorValue(List<T> resultList) throws Exception {
        TableSqlBuilder<T> tableModel = TableInfoCache.getTableModel(thisClass);

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
    private void oneToManyHandler(TableSqlBuilder<T> tableModel, List<T> entityList) throws Exception {
        List<Field> oneToManyFieldList = tableModel.getOneToManyFieldList();
        if (JudgeUtil.isNotEmpty(oneToManyFieldList)) {
            for (Field waitSetField : oneToManyFieldList) {
                DbJoinToManyParseModel joinToManyParseModel = new DbJoinToManyParseModel(waitSetField);
                Class<?> joinTarget = joinToManyParseModel.getJoinTarget();
                TableSqlBuilder<?> targetTableModel = TableInfoCache.getTableModel(joinTarget);
                String condition = joinToManyParseModel.queryCondition();

                for (T entity : entityList) {
                    if (entity == null) {
                        continue;
                    }
                    List<?> queryResult = this.queryResult(entity, joinToManyParseModel.getThisField(), condition,
                            joinTarget, targetTableModel);
                    if (queryResult == null) {
                        continue;
                    }
                    if (JudgeUtil.isNotEmpty(queryResult)) {
                        CustomUtil.writeFieldValue(queryResult, entity,
                                waitSetField.getName(), joinToManyParseModel.getJoinTarget());
                    }
                }
            }
        }
    }



    /**
     * 一对一的值set
     * @param tableModel - 实体解析模板
     * @param entityList - 实体对象集合
     */
    private void oneToOneHandler(TableSqlBuilder<T> tableModel, List<T> entityList) throws Exception {
        List<Field> oneToOneFieldList = tableModel.getOneToOneFieldList();
        if (JudgeUtil.isEmpty(oneToOneFieldList)) {
            return;
        }
        for (Field waitSetField : oneToOneFieldList) {
            DbJoinToOneParseModel joinToOneParseModel = new DbJoinToOneParseModel(waitSetField);
            Class<?> joinTarget = joinToOneParseModel.getJoinTarget();
            TableSqlBuilder<?> targetTableModel = TableInfoCache.getTableModel(joinTarget);
            String condition = joinToOneParseModel.queryCondition();

            for (T entity : entityList) {
                if (entity == null) {
                    continue;
                }
                List<?> queryResult = this.queryResult(entity, joinToOneParseModel.getThisField(), condition,
                        joinTarget, targetTableModel);
                if (JudgeUtil.isNotEmpty(queryResult)) {
                    if (queryResult.get(0) == null) {
                        continue;
                    } else if (queryResult.size() > 1 && joinToOneParseModel.isThrowErr()) {
                        ExThrowsUtil.toCustom(joinToOneParseModel.getJoinTarget() + "One to one query, but %s results are found", queryResult.size());
                    }
                    CustomUtil.writeFieldValue(queryResult.get(0), entity,
                            waitSetField.getName(), joinToOneParseModel.getJoinTarget());
                }
            }
        }
    }


    /**
     * 查询结果
     * @param entity 当前待注入的实体对象
     * @param thisField 查询的关联java字段
     * @param condition 查询的条件
     * @param joinTarget 查询的对象
     * @param targetTableModel 查询对象的注解模板解析对象
     * @return Lists
     * @throws Exception
     */
    private List<?> queryResult(T entity, String thisField, String condition, Class<?> joinTarget, TableSqlBuilder<?> targetTableModel) throws Exception {
        try {
            Object queryValue = CustomUtil.readFieldValue(entity, thisField);
            if (queryValue == null) {
                return null;
            }

            // 若该表存在逻辑删除的字段，则处理逻辑删除条件
            FullSqlConditionExecutor conditionExecutor = sqlExecutor.handleLogicWithCondition(targetTableModel.getAlias(), condition,
                    sqlExecutor.getLogicDeleteQuerySql(), targetTableModel.getTable());

            // 构建该对象的查询sql模板
            AbstractSqlBuilder<?> abstractSqlBuilder = sqlExecutor.buildSqlOperationTemplate(joinTarget);

            String selectSql = abstractSqlBuilder.buildSql() + conditionExecutor.execute();
            return sqlExecutor.executeQueryNotPrintSql(joinTarget, selectSql, queryValue);

        }catch (NoSuchFieldException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


}
