package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.fieldfill.ColumnFillAutoHandler;
import com.custom.action.fieldfill.TableFillObject;
import com.custom.action.util.DbUtil;
import com.custom.comm.utils.CustomApplicationUtil;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.Constants;
import com.custom.comm.enums.FillStrategy;
import com.custom.comm.exceptions.ExThrowsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

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
                deleteSql = String.format(DbUtil.LOGIC_DELETE_TEMPLATE,
                        getTable(),
                        getAlias(),
                        getLogicDeleteUpdateSql()
                );
            } else {
                deleteSql = String.format(DbUtil.DELETE_TEMPLATE, getTable(), getAlias());
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return deleteSql;
    }


    /**
     * 在删除数据时，若是有逻辑删除，则在逻辑删除后，进行固定字段的自动填充
     */
    protected void handleLogicDelAfter(Class<?> t, String condition, Object... params) {
        ColumnFillAutoHandler fillColumnHandler = CustomApplicationUtil.getBean(ColumnFillAutoHandler.class);
        if (Objects.isNull(fillColumnHandler)) {
            return;
        }
        Optional<TableFillObject> first = fillColumnHandler.fillStrategy().stream()
                .filter(x -> x.getEntityClass().equals(t)).findFirst();

        if (!first.isPresent()) {
            first = fillColumnHandler.fillStrategy().stream()
                    .filter(x -> x.getEntityClass().isAssignableFrom(t)).findFirst();
        }
        if (!first.isPresent()) {
            return;
        }
        TableFillObject op = first.get();

        String autoUpdateWhereSqlCondition = Constants.WHERE + getLogicDeleteUpdateSql() + condition;

        FillStrategy strategy = op.getStrategy();
        if (strategy == FillStrategy.DEFAULT) {
            return;
        }
        String autoUpdateSql = buildLogicDelAfterAutoUpdateSql(strategy, autoUpdateWhereSqlCondition, params);
        if (!ObjectUtils.isEmpty(autoUpdateSql)) {
            try {
                executeUpdateNotPrintSql(autoUpdateSql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 自动填充的sql构造（采用逻辑删除后进行Update操作的方式进行自动填充）
     */
    private String buildLogicDelAfterAutoUpdateSql(FillStrategy strategy, String whereKeySql, Object... params) {
        StringBuilder autoUpdateSql = new StringBuilder();
        Optional<TableFillObject> first = Objects.requireNonNull(CustomApplicationUtil.getBean(ColumnFillAutoHandler.class))
                .fillStrategy().stream().filter(x -> x.getEntityClass().equals(getEntityClass())).findFirst();
        first.ifPresent(op -> {
            autoUpdateSql.append(Constants.UPDATE)
                    .append(getTable())
                    .append(" ")
                    .append(getAlias())
                    .append(Constants.SET);

            if (strategy.toString().contains(op.getStrategy().toString())) {
                String sqlFragment = buildAssignAutoUpdateSqlFragment(op.getTableFillMapper());
                if (Objects.nonNull(sqlFragment)) {
                    autoUpdateSql.append(sqlFragment);
                }
                autoUpdateSql.append(CustomUtil.handleExecuteSql(whereKeySql, params));
            }
        });
        return autoUpdateSql.toString();
    }

    /**
     * 构建指定逻辑删除时自动填充的sql片段
     */
    private String buildAssignAutoUpdateSqlFragment(Map<String, Object> tableFillObjects) {
        StringJoiner autoUpdateFieldSql = new StringJoiner(Constants.SEPARATOR_COMMA_2);
        StringBuilder updateField;
        if (ObjectUtils.isEmpty(tableFillObjects)) {
            return autoUpdateFieldSql.toString();
        }
        for (String fieldName : tableFillObjects.keySet()) {
            if (ObjectUtils.isEmpty(getFieldMapper().get(fieldName))) {
                ExThrowsUtil.toCustom("未找到可匹配的java属性字段");
            }
            updateField = new StringBuilder();
            Object fieldVal = tableFillObjects.get(fieldName);
            if (ObjectUtils.isEmpty(fieldVal)) continue;
            updateField.append(getFieldMapper().get(fieldName)).append(Constants.EQUALS).append(fieldVal);
            autoUpdateFieldSql.add(updateField);
            getFieldParserModels().stream().filter(x -> x.getFieldName().equals(fieldName)).findFirst().ifPresent(op -> {
                op.setValue(fieldVal);
            });
        }
        return autoUpdateFieldSql.toString();
    }

    public HandleDeleteSqlBuilder(Class<T> entityClass) {
        TableParseModel<T> tableSqlBuilder = TableInfoCache.getTableModel(entityClass);
        this.injectTableInfo(tableSqlBuilder);
    }
}
