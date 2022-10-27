package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.fieldfill.ColumnFillAutoHandler;
import com.custom.action.fieldfill.TableFillObject;
import com.custom.action.util.DbUtil;
import com.custom.comm.enums.FillStrategy;
import com.custom.comm.enums.SqlExecTemplate;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.CustomApplicationUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Xiao-Bai
 * @date 2022/4/10 15:07
 * @desc:构建删除sql
 */
public class HandleDeleteSqlBuilder<T> extends AbstractSqlBuilder<T> {

    private static final Logger logger = LoggerFactory.getLogger(HandleDeleteSqlBuilder.class);

    @Override
    public String createTargetSql() {
        String deleteSql = "";
        try {
            boolean isExist = checkLogicFieldIsExist();
            if (isExist) {
                String setColumnSql = getLogicDeleteUpdateSql();
                String customFillSql = this.handleLogicDelBefore();

                if (StrUtils.isNotBlank(customFillSql)) {
                    setColumnSql = setColumnSql + Constants.SEPARATOR_COMMA_2 + customFillSql;
                }

                // 逻辑删除
                deleteSql = SqlExecTemplate.format(SqlExecTemplate.LOGIC_DELETE, getTable(), getAlias(), setColumnSql);

            } else {

                // 物理删除
                deleteSql = SqlExecTemplate.format(SqlExecTemplate.DELETE_DATA, getTable(), getAlias());
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return deleteSql;
    }


    /**
     * 在删除数据时，若是有逻辑删除，则在逻辑删除前，进行固定字段的自动填充
     * <br/> 例如: 修改时间，修改人
     */
    protected String handleLogicDelBefore() {
        ColumnFillAutoHandler fillColumnHandler = CustomApplicationUtil.getBean(ColumnFillAutoHandler.class);
        if (Objects.isNull(fillColumnHandler)) {
            return null;
        }
        Class<T> entityClass = getEntityClass();
        Optional<TableFillObject> first = fillColumnHandler.fillStrategy().stream()
                .filter(x -> x.getEntityClass().equals(entityClass)).findFirst();

        if (!first.isPresent()) {
            first = fillColumnHandler.fillStrategy().stream()
                    .filter(x -> x.getEntityClass().isAssignableFrom(entityClass)).findFirst();
        }
        if (!first.isPresent()) {
            return null;
        }
        TableFillObject fillObject = first.get();

        FillStrategy strategy = fillObject.getStrategy();
        if (strategy == FillStrategy.DEFAULT) {
            return null;
        }
        return this.buildAssignAutoUpdateSqlFragment(fillObject.getTableFillMapper());
    }

    /**
     * 构建指定逻辑删除时自动填充的sql片段
     */
    private String buildAssignAutoUpdateSqlFragment(Map<String, Object> tableFillObjects) {
        StringJoiner autoUpdateFieldSql = new StringJoiner(Constants.SEPARATOR_COMMA_2);

        Map<String, String> fieldMapper = getFieldMapper();
        if (JudgeUtil.isEmpty(tableFillObjects)) {
            return autoUpdateFieldSql.toString();
        }
        for (String fieldName : tableFillObjects.keySet()) {
            String column = fieldMapper.get(fieldName);
            if (JudgeUtil.isEmpty(column)) {
                ExThrowsUtil.toCustom("未找到可匹配的java属性字段");
            }

            Object fieldVal = tableFillObjects.get(fieldName);
            // 若自定义的值为null, 则跳过
            if (fieldVal == null) continue;

            String updateField = DbUtil.formatMapperSqlCondition(column, fieldVal);
            autoUpdateFieldSql.add(updateField);
        }
        return autoUpdateFieldSql.toString();
    }

    public HandleDeleteSqlBuilder(Class<T> entityClass, int order) {
        TableParseModel<T> tableSqlBuilder = TableInfoCache.getTableModel(entityClass);
        this.injectTableInfo(tableSqlBuilder, order);
    }
}
