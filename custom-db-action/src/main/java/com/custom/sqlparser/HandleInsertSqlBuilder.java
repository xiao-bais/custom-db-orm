package com.custom.sqlparser;

import com.custom.comm.ConvertUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbaction.AbstractSqlBuilder;
import com.custom.dbconfig.SymbolConst;
import com.custom.fieldfill.FieldAutoFillHandleUtils;
import com.custom.sqlparser.TableInfoCache;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author Xiao-Bai
 * @date 2022/4/3 17:26
 * @desc:提供一系列新增记录的sql构建
 */
public class HandleInsertSqlBuilder<T> extends AbstractSqlBuilder<T> {

    private final StringBuilder insertSql;
    private List<T> entityList;
    private String logicColumn;
    private Object logicValue;

    public HandleInsertSqlBuilder() {
        this.insertSql = new StringBuilder();
    }


    @Override
    protected String buildSql() {
        StringJoiner insertColumn = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        if (Objects.nonNull(getKeyParserModel())) {
            insertColumn.add(getKeyParserModel().getDbKey());
        }
        if (!getFieldParserModels().isEmpty()) {
            getFieldParserModels().forEach(x -> insertColumn.add(x.getColumn()));
        }
        return String.format("insert into %s(%s) values %s ", getTable(), insertColumn, getInsertSymbol(logicColumn, logicValue));
    }

    /**
     * 获取添加时的？
     * @param logicColumn 逻辑删除的字段
     * @param val 未逻辑删除的值
     */
    private String getInsertSymbol(String logicColumn, Object val) {
        StringJoiner insertSymbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_1);
        for (T currEntity : entityList) {
            setEntity(currEntity);
            StringJoiner brackets = new StringJoiner(SymbolConst.SEPARATOR_COMMA_1, SymbolConst.BRACKETS_LEFT, SymbolConst.BRACKETS_RIGHT);
            if (Objects.nonNull(getKeyParserModel())) {
                brackets.add(SymbolConst.QUEST);
                this.getSqlParams().add(getKeyParserModel().getValue());
            }
            getFieldParserModels().forEach(x -> {
                Object fieldValue = x.getValue();
                if (FieldAutoFillHandleUtils.exists(getEntityClass(), x.getFieldName())
                        && Objects.isNull(fieldValue) ) {
                    fieldValue = FieldAutoFillHandleUtils.getFillValue(getEntityClass(), x.getFieldName());
                    x.setValue(fieldValue);
                }else if(JudgeUtilsAx.isNotEmpty(logicColumn) && TableInfoCache.isExistsLogic(getTable())  && x.getColumn().equals(logicColumn)) {
                    fieldValue = ConvertUtil.transToObject(x.getType(), val);
                    x.setValue(fieldValue);
                }
                this.getSqlParams().add(fieldValue);
                brackets.add(SymbolConst.QUEST);
            });
            insertSymbol.add(brackets.toString());
        }
        return insertSymbol.toString();
    }



    public String getInsertSql() {
        return insertSql.toString();
    }

    public void setEntityList(List<T> entityList) {
        this.entityList = entityList;
    }

    public String getLogicColumn() {
        return logicColumn;
    }

    public void setLogicColumn(String logicColumn) {
        this.logicColumn = logicColumn;
    }

    public Object getLogicValue() {
        return logicValue;
    }

    public void setLogicValue(Object logicValue) {
        this.logicValue = logicValue;
    }
}
