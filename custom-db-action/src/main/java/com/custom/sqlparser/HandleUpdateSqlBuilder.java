package com.custom.sqlparser;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbaction.AbstractSqlBuilder;
import com.custom.dbconfig.SymbolConst;
import com.custom.exceptions.ExThrowsUtil;
import com.custom.fieldfill.FieldAutoFillHandleUtils;
import com.custom.wrapper.SFunction;
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

    private final StringBuilder updateSql;
    private final StringJoiner updateSqlColumns;
    private String condition;
    private List<Object> conditionVals;
    private SFunction<T, ?>[] updateFuncColumns;
    private String[] updateStrColumns;

    public HandleUpdateSqlBuilder() {
        updateSql = new StringBuilder();
        updateSqlColumns = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
    }


    /**
     * 构建修改的sql字段语句(条件构造器使用)
     */
    @Override
    public String buildSql() {
        // 修改字段构建
        updateSqlField();
        String conditions;
        if (JudgeUtilsAx.isEmpty(condition)) {
            conditions = updateKeyCondition();
        }else {
            conditions = updateCustomCondition();
        }
        updateSql.append(SymbolConst.UPDATE)
                .append(getTable()).append(" ")
                .append(getAlias())
                .append(SymbolConst.SET)
                .append(updateSqlColumns)
                .append(SymbolConst.WHERE)
                .append(conditions);

        return updateSql.toString();
    }

    /**
     * 获取修改的逻辑删除字段sql（以主键做条件去修改）
     */
    private String updateKeyCondition() {
        String keySqlField = getKeyParserModel().getFieldSql();
        String condition = null;
        try {
            condition = checkLogicFieldIsExist() ? String.format("%s and %s = ?", getLogicDeleteQuerySql(), keySqlField) : String.format("%s = ?", keySqlField);
            Object keyVal = getKeyParserModel().getValue();
            if (Objects.isNull(keyVal)) {
                ExThrowsUtil.toNull("主键的值缺失");
            }
            getSqlParams().add(keyVal);
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
            condition = checkLogicFieldIsExist() ?  getLogicDeleteQuerySql() + this.condition : CustomUtil.trimSqlCondition(this.condition);
            getSqlParams().addAll(this.conditionVals);
        } catch (Exception e) {
            ExThrowsUtil.toCustom(e.toString());
        }
        return condition;
    }




    /**
     * 修改字段构建（set之后 where之前）
     */
    private void updateSqlField() {
        if (Objects.nonNull(updateFuncColumns)) {
            buildFixedFieldSql(true);
        } else if(Objects.nonNull(updateStrColumns)) {
            buildFixedFieldSql(false);
        } else {
            getFieldParserModels().forEach(x -> {
                Object value = x.getValue();
                if (Objects.isNull(value)) {
                    // 修改时的自动填充，只有在填充字段为null的前提下，才进行填充
                    Object fillValue = FieldAutoFillHandleUtils.getFillValue(getEntityClass(), x.getFieldName());
                    if (Objects.nonNull(fillValue)) {
                        value = fillValue;
                    }
                }
                if(Objects.nonNull(value)) {
                    updateSqlColumns.add(String.format("%s = ?", x.getFieldSql()));
                    getSqlParams().add(value);
                }
            });
        }
    }

    /**
     * 以指定修改的字段去构建修改的sql
     */
    private void buildFixedFieldSql(boolean isFunc) {
        String[] updateColumns = isFunc ? getColumnParseHandler().getColumn(this.updateFuncColumns) : this.updateStrColumns;
        for (String column : updateColumns) {
            Optional<DbFieldParserModel<T>> updateFieldOP = getFieldParserModels().stream().filter(x -> x.getColumn().equals(column)).findFirst();
            updateFieldOP.ifPresent(op -> {
                updateSqlColumns.add(String.format("%s = ?", op.getFieldSql()));
                getSqlParams().add(op.getValue());
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
