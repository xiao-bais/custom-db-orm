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

    public HandleUpdateSqlBuilder() {
        updateSql = new StringBuilder();
    }


    @Override
    protected String buildSql() {
        return null;
    }

    /**
     * 构建修改的sql字段语句
     */
    protected void buildUpdateWrapper(String condition, List<Object> conditionVals) {
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
    }

    /**
     * 获取修改的逻辑删除字段sql
     */
    private String getLogicUpdateSql(String key, String logicDeleteQuerySql) {
        return JudgeUtilsAx.isNotBlank(logicDeleteQuerySql) ? String.format("%s.%s and %s = ?", getAlias(), logicDeleteQuerySql, key) : String.format("%s = ?", key);
    }

    /**
     * 构建修改的sql语句
     */
    protected void buildUpdateSql(String[] updateDbFields, String logicDeleteQuerySql) {
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
                .append(getLogicUpdateSql(getKeyParserModel().getFieldSql(), logicDeleteQuerySql));
        getSqlParams().add(getKeyParserModel().getValue(getEntity()));
    }
}
