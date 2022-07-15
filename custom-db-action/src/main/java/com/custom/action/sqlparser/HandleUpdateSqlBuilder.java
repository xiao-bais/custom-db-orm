package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.fieldfill.ColumnAutoFillHandleUtils;
import com.custom.action.util.DbUtil;
import com.custom.action.wrapper.SFunction;
import com.custom.comm.JudgeUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.exceptions.ExThrowsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * @author Xiao-Bai
 * @date 2022/4/10 15:01
 * @desc:构建修改sql
 */
public class HandleUpdateSqlBuilder<T> extends AbstractSqlBuilder<T> {

    private static final Logger logger = LoggerFactory.getLogger(HandleUpdateSqlBuilder.class);

    /**
     * 修改的最终sql
     */
    private final StringBuilder updateSql;
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
    /**
     * 指定要修改的字段-字段字符串数组
     */
    private String[] updateStrColumns;

    public HandleUpdateSqlBuilder() {
        updateSql = new StringBuilder();
        updateSqlColumns = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
    }


    /**
     * 构建修改的sql字段语句(条件构造器使用)
     */
    @Override
    public String buildSql() {
        // 修改字段构建
        this.updateSqlFieldBuilder();
        String updateCondition = JudgeUtil.isEmpty(condition) ? this.updateKeyCondition() : this.updateCustomCondition();
        return updateSql
                .append(SymbolConstant.UPDATE)
                .append(getTable()).append(" ")
                .append(getAlias())
                .append("\n")
                .append(SymbolConstant.SET)
                .append(updateSqlColumns)
                .append(DbUtil.whereSqlCondition(updateCondition)).toString();
    }

    /**
     * 获取修改的逻辑删除字段sql（以主键做条件去修改）
     */
    private String updateKeyCondition() {
        DbKeyParserModel<T> keyParserModel = getKeyParserModel();
        String keySqlField = keyParserModel.getFieldSql();
        String condition = null;
        try {
            String formatSetSql = DbUtil.formatSetSql(keySqlField);
            condition = checkLogicFieldIsExist() ? DbUtil.formatSetConditionSql(getLogicDeleteQuerySql(), formatSetSql) : formatSetSql;
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
            chooseAppointFieldSql(true);
            return;
        }
        if (Objects.nonNull(updateStrColumns)) {
            chooseAppointFieldSql(false);
            return;
        }
        for (DbFieldParserModel<T> field : getFieldParserModels()) {
            Object value = field.getValue();
            if (Objects.isNull(value)) {
                // 修改时必要的自动填充
                // 当修改时，用户没有为自动填充的字段额外设置业务值，则启用原本设定的默认值进行填充
                Object fillValue = ColumnAutoFillHandleUtils.getFillValue(getEntityClass(), field.getFieldName());
                if (Objects.nonNull(fillValue)) {
                    value = fillValue;
                }
            }
            if (Objects.nonNull(value)) {
                updateSqlColumns.add(DbUtil.formatSetSql(field.getFieldSql()));
                addParams(value);
            }
        }
    }


    /**
     * 选择以指定修改的字段去构建修改的sql
     * @param isFunc 是否使用Function函数表达式
     */
    private void chooseAppointFieldSql(boolean isFunc) {
        String[] updateColumns = isFunc ? getColumnParseHandler().getColumn(this.updateFuncColumns) : this.updateStrColumns;
        for (String column : updateColumns) {
            Optional<DbFieldParserModel<T>> updateFieldOP = getFieldParserModels().stream().filter(x -> x.getFieldSql().equals(column)).findFirst();
            updateFieldOP.ifPresent(op -> {
                updateSqlColumns.add(DbUtil.formatSetSql(op.getFieldSql()));
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

    protected void setUpdateStrColumns(String[] updateStrColumns) {
        this.updateStrColumns = updateStrColumns;
    }
}
