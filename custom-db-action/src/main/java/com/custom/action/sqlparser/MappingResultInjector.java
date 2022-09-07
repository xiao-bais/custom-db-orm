package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractSqlExecutor;
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
 * @desc 一对一，一对多 value植入
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


    public void injectorValue(List<T> resultList) {
        TableSqlBuilder<T> tableModel = TableInfoCache.getTableModel(thisClass);
        for (T entity : resultList) {
            if (entity == null) {
                continue;
            }

            // set 一对一
            this.oneToOneHandler(tableModel, entity);

            // set 一对多
            this.oneToManyHandler(tableModel, entity);

        }

    }

    /**
     * 一对多的值set
     * @param tableModel - 实体解析模板
     * @param entity - 实体对象
     */
    private void oneToManyHandler(TableSqlBuilder<T> tableModel, T entity) {
        List<Field> oneToManyFieldList = tableModel.getOneToManyFieldList();
        if (JudgeUtil.isNotEmpty(oneToManyFieldList)) {
            for (Field waitSetField : oneToManyFieldList) {
                DbJoinToManyParseModel joinToManyParseModel = new DbJoinToManyParseModel(waitSetField);

                try {
                    Object queryValue = CustomUtil.readFieldValue(entity, joinToManyParseModel.getThisField());
                    if (queryValue == null) {
                        continue;
                    }
                    List<?> queryResult = sqlExecutor.selectList(joinToManyParseModel.getJoinTarget(), joinToManyParseModel.queryCondition(), queryValue);

                    if (JudgeUtil.isNotEmpty(queryResult)) {
                        CustomUtil.writeFieldValue(queryResult, entity,
                                waitSetField.getName(), joinToManyParseModel.getJoinTarget());
                    }
                } catch (NoSuchFieldException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 一对一的值set
     * @param tableModel - 实体解析模板
     * @param entity - 实体对象
     */
    private void oneToOneHandler(TableSqlBuilder<T> tableModel, T entity) {
        List<Field> oneToOneFieldList = tableModel.getOneToOneFieldList();
        if (JudgeUtil.isEmpty(oneToOneFieldList)) {
            return;
        }
        for (Field waitSetField : oneToOneFieldList) {
            DbJoinToOneParseModel joinToOneParseModel = new DbJoinToOneParseModel(waitSetField);

            try {
                Object queryValue = CustomUtil.readFieldValue(entity, joinToOneParseModel.getThisField());
                if (queryValue == null) {
                    continue;
                }
                List<?> queryResult = sqlExecutor.selectList(joinToOneParseModel.getJoinTarget(), joinToOneParseModel.queryCondition(), queryValue);

                if (queryResult.size() > 1 && joinToOneParseModel.isThrowErr()) {
                    ExThrowsUtil.toCustom(joinToOneParseModel.getJoinTarget() + "One to one query, but %s results are found", queryResult.size());
                }
                if (queryResult.get(0) == null) {
                    continue;
                }

                if (JudgeUtil.isNotEmpty(queryResult)) {
                    CustomUtil.writeFieldValue(queryResult.get(0), entity,
                            waitSetField.getName(), joinToOneParseModel.getJoinTarget());
                }
            } catch (NoSuchFieldException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


}
