package com.custom.sqlparser;

import com.custom.comm.JudgeUtilsAx;
import com.custom.dbaction.AbstractSqlBuilder;
import com.custom.dbconfig.SymbolConst;
import com.custom.fieldfill.FieldAutoFillHandleUtils;
import com.custom.wrapper.ColumnParseHandler;
import com.custom.wrapper.SFunction;
import com.custom.wrapper.SelectFunc;

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

        getUpdateSqlField();

        return updateSql.toString();
    }

    /**
     * 获取修改的逻辑删除字段sql
     */
    private String getLogicUpdateSql(String key) {
        return JudgeUtilsAx.isNotBlank(getLogicDeleteQuerySql()) ? String.format("%s and %s = ?", getLogicDeleteQuerySql(), key) : String.format("%s = ?", key);
    }


    private void getUpdateSqlField() {
        if (Objects.nonNull(updateFuncColumns) && updateFuncColumns.length > 0) {
            buildFixedFieldSql(true);
        } else if(Objects.nonNull(updateStrColumns) && updateStrColumns.length > 0) {
            buildFixedFieldSql(false);
        } else {
            getFieldParserModels().forEach(x -> {
                Object value = x.getValue();
                if (Objects.nonNull(value)) {
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

    /**
     * 构建自动填充策略的修改sql字段
     */
    private void buildAutoFillSqlColumn() {
//        FieldAutoFillHandleUtils.getFillValue()

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
