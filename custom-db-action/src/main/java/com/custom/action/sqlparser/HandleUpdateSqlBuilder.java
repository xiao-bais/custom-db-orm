package com.custom.action.sqlparser;

import com.custom.action.condition.DefaultColumnParseHandler;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.fieldfill.ColumnAutoFillHandleUtils;
import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.action.util.DbUtil;
import com.custom.action.condition.SFunction;
import com.custom.comm.JudgeUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.exceptions.ExThrowsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Xiao-Bai
 * @date 2022/4/10 15:01
 * @desc:构建修改sql
 */
public class HandleUpdateSqlBuilder<T> extends AbstractSqlBuilder<T> {

    private static final Logger logger = LoggerFactory.getLogger(HandleUpdateSqlBuilder.class);


    /**
     * 修改的字段 set部分的sql
     */
    private final StringJoiner updateSqlColumns;
    /**
     * 修改的条件
     */
    private String condition;
    /**
     * sql参数值
     */
    private List<Object> conditionVals;
    /**
     * 指定要修改的字段-函数表达式
     */
    private SFunction<T, ?>[] updateFuncColumns;


    public HandleUpdateSqlBuilder() {
        updateSqlColumns = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
    }

    public HandleUpdateSqlBuilder(Class<T> entityClass) {
        updateSqlColumns = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
        TableSqlBuilder<T> tableSqlBuilder = TableInfoCache.getTableModel(entityClass);
        this.injectTableInfo(tableSqlBuilder);
    }

    @SuppressWarnings("unchecked")
    public HandleUpdateSqlBuilder(T entity) {
        this((Class<T>) entity.getClass());
    }


    /**
     * 构建修改的sql字段语句(条件构造器使用)
     */
    @Override
    public String createTargetSql() {
        // 修改字段构建
        this.updateSqlFieldBuilder();
        String updateCondition = JudgeUtil.isEmpty(condition) ? this.updateKeyCondition() : this.updateCustomCondition();
        setEntity(null);
        return String.format(DbUtil.UPDATE_TEMPLATE,
                getTable(), getAlias(),
                updateSqlColumns,
                DbUtil.whereSqlCondition(updateCondition)
        );
    }

    /**
     * 获取修改的逻辑删除字段sql（以主键做条件去修改）
     */
    private String updateKeyCondition() {
        DbKeyParserModel<T> keyParserModel = getKeyParserModel();
        String keySqlField = keyParserModel.getFieldSql();
        String condition = null;
        try {
            String formatSetSql = DbUtil.formatSqlCondition(keySqlField);
            condition = checkLogicFieldIsExist() ? DbUtil.formatSqlCondition(getLogicDeleteQuerySql(), formatSetSql) : formatSetSql;
            Object keyVal = keyParserModel.getValue();
            if (Objects.isNull(keyVal)) {
                ExThrowsUtil.toNull("主键的值缺失");
            }
            addParams(keyVal);
        } catch (Exception e) {
            ExThrowsUtil.toCustom(e.toString());
        }
        return condition;
    }

    /**
     * 自定义修改的条件
     */
    private String updateCustomCondition() {
        String condition = null;
        try {
            condition = checkLogicFieldIsExist() ? (getLogicDeleteQuerySql() + " " + this.condition) : DbUtil.trimSqlCondition(this.condition);
            addParams(this.conditionVals);
        } catch (Exception e) {
            ExThrowsUtil.toCustom(e.toString());
        }
        return condition;
    }




    /**
     * 修改字段构建（set之后 where之前）
     */
    private void updateSqlFieldBuilder() {
        if (Objects.nonNull(updateFuncColumns)) {
            this.chooseAppointFieldSql();
            return;
        }
        for (DbFieldParserModel<T> field : getFieldParserModels()) {
            Object value = field.getValue();
            if (Objects.isNull(value)) {
                // 修改时必要的自动填充
                // 当修改时，用户没有为自动填充的字段额外设置业务值，则启用原本设定的默认值进行填充
                Object fillValue = ColumnAutoFillHandleUtils
                        .getFillValue(getEntityClass(), field.getFieldName());
                if (Objects.nonNull(fillValue)) {
                    value = fillValue;
                }
            }
            if (Objects.nonNull(value)) {
                updateSqlColumns.add(DbUtil.formatSqlCondition(field.getFieldSql()));
                addParams(value);
            }
        }
    }


    /**
     * 选择以指定修改的字段去构建修改的sql
     * @param isFunc 是否使用Function函数表达式
     */
    private void chooseAppointFieldSql() {
        ColumnParseHandler<T> columnParseHandler = getColumnParseHandler();
        List<String> updateColumns = columnParseHandler.parseToColumns(Arrays.asList(updateFuncColumns));
        for (String column : updateColumns) {
            Optional<DbFieldParserModel<T>> updateFieldOP = getFieldParserModels().stream().filter(x -> x.getFieldSql().equals(column)).findFirst();
            updateFieldOP.ifPresent(op -> {
                updateSqlColumns.add(DbUtil.formatSqlCondition(op.getFieldSql()));
                addParams(op.getValue());
            });
        }
    }

    protected void setCondition(String condition) {
        this.condition = condition;
    }

    protected void setConditionVals(List<Object> conditionVals) {
        this.conditionVals = conditionVals;
    }

    protected void setUpdateFuncColumns(SFunction<T, ?>[] updateFuncColumns) {
        this.updateFuncColumns = updateFuncColumns;
    }
}
