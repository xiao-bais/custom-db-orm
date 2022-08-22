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
public class OneToResultInjector<T> {

    private final Logger logger = LoggerFactory.getLogger(OneToResultInjector.class);

    /**
     * 主表的对象
     */
    private final Class<T> thisClass;

    /**
     * select 查询对象
     */
    public AbstractSqlExecutor sqlExecutor;

    public OneToResultInjector(Class<T> thisClass, AbstractSqlExecutor sqlExecutor) {
        this.thisClass = thisClass;
        this.sqlExecutor = sqlExecutor;
    }


    public void injectorValue(List<T> resultList) {
        TableSqlBuilder<T> tableModel = TableInfoCache.getTableModel(thisClass);
        for (T entity : resultList) {

            // set 一对一
            this.oneToOneHandler(tableModel, entity);

            // set 一对多
            List<Field> oneToManyFieldList = tableModel.getOneToManyFieldList();
            if (JudgeUtil.isNotEmpty(oneToManyFieldList)) {
                for (Field waitSetField : oneToManyFieldList) {

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

                if (JudgeUtil.isNotEmpty(queryResult)) {
                    CustomUtil.writeFieldValue(entity, waitSetField.getName(), queryResult.get(0));
                }
            } catch (NoSuchFieldException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


}
