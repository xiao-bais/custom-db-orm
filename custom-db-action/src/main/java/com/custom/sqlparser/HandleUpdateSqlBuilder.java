package com.custom.sqlparser;

import com.custom.comm.JudgeUtilsAx;
import com.custom.dbaction.AbstractSqlBuilder;
import com.custom.dbconfig.SymbolConst;

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

    private final StringBuilder updateSql;
    private String condition;
    private List<Object> conditionVals;
    private String[] updateDbFields;

    public HandleUpdateSqlBuilder() {
        updateSql = new StringBuilder();
    }


    /**
     * 构建修改的sql字段语句
     */
    @Override
    public String buildSql() {
        StringJoiner updateFieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        for (DbFieldParserModel<T> fieldParserModel : getFieldParserModels()) {
            Object value = fieldParserModel.getValue();
            if (Objects.nonNull(value)) {
                updateFieldSql.add(fieldParserModel.getFieldSql() + " = ?");
                getSqlParams().add(value);
            }
        }
        updateSql.append(SymbolConst.UPDATE).append(getTable())
                .append(" ").append(getAlias())
                .append(SymbolConst.SET).append(updateFieldSql)
                .append(" ").append(condition);
        getSqlParams().addAll(conditionVals);
        return updateSql.toString();
    }

    /**
     * 获取修改的逻辑删除字段sql
     */
    private String getLogicUpdateSql(String key) {
        return JudgeUtilsAx.isNotBlank(getLogicDeleteQuerySql()) ? String.format("%s.%s and %s = ?", getAlias(), getLogicDeleteQuerySql(), key) : String.format("%s = ?", key);
    }

    /**
     * 构建修改的sql语句
     */
    protected void buildUpdateSql() {
        StringJoiner updateFieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        if (updateDbFields.length > 0) {
            for (String field : updateDbFields) {
                Optional<DbFieldParserModel<T>> updateFieldOP = getFieldParserModels().stream().filter(x -> x.getColumn().equals(field)).findFirst();
                updateFieldOP.ifPresent(op -> {
                    updateFieldSql.add(String.format("%s = ?", op.getFieldSql()));
                    getSqlParams().add(op.getValue());
                });
            }
        } else {
            getFieldParserModels().forEach(x -> {
                Object value = x.getValue();
                if (Objects.nonNull(value)) {
                    updateFieldSql.add(String.format("%s = ?", x.getFieldSql()));
                    getSqlParams().add(value);
                }
            });
        }
        updateSql.append(SymbolConst.UPDATE).append(getTable()).append(" ").append(getAlias())
                .append(SymbolConst.SET).append(updateFieldSql).append(SymbolConst.WHERE)
                .append(getLogicUpdateSql(getKeyParserModel().getFieldSql()));
        getSqlParams().add(getKeyParserModel().getValue(getEntity()));
    }

    protected void setCondition(String condition) {
        this.condition = condition;
    }

    protected void setConditionVals(List<Object> conditionVals) {
        this.conditionVals = conditionVals;
    }

    protected void setUpdateDbFields(String[] updateDbFields) {
        this.updateDbFields = updateDbFields;
    }
}
