package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.fieldfill.FieldAutoFillHandleUtils;
import com.custom.comm.ConvertUtil;
import com.custom.comm.SymbolConstant;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author Xiao-Bai
 * @date 2022/4/3 17:26
 * @desc:提供一系列新增记录的sql构建
 */
public class HandleInsertSqlBuilder<T> extends AbstractSqlBuilder<T> {


    @Override
    public String buildSql() {
        StringJoiner insertColumn = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
        if (Objects.nonNull(getKeyParserModel())) {
            insertColumn.add(getKeyParserModel().getDbKey());
        }
        if (!getFieldParserModels().isEmpty()) {
            getFieldParserModels().forEach(x -> insertColumn.add(x.getColumn()));
        }
        return String.format("insert into %s(%s) values %s ", getTable(), insertColumn, getInsertSymbol());
    }


    /**
     * 获取添加时的？
     */
    private String getInsertSymbol() {
        StringJoiner insertSymbol = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_1);
        for (T currEntity : getEntityList()) {
            setEntity(currEntity);
            StringJoiner brackets = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_1, SymbolConstant.BRACKETS_LEFT, SymbolConstant.BRACKETS_RIGHT);
            if (Objects.nonNull(getKeyParserModel())) {
                brackets.add(SymbolConstant.QUEST);
                this.getSqlParams().add(getKeyParserModel().getValue());
            }
            getFieldParserModels().forEach(x -> {
                Object fieldValue = x.getValue();
                if (FieldAutoFillHandleUtils.exists(getEntityClass(), x.getFieldName())
                        && Objects.isNull(fieldValue) ) {
                    fieldValue = FieldAutoFillHandleUtils.getFillValue(getEntityClass(), x.getFieldName());
                    x.setValue(fieldValue);
                }else {
                    try {
                        if(checkLogicFieldIsExist() && x.getColumn().equals(getLogicColumn())) {
                            fieldValue = ConvertUtil.transToObject(x.getType(), getLogicNotDeleteValue());
                            x.setValue(fieldValue);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                this.getSqlParams().add(fieldValue);
                brackets.add(SymbolConstant.QUEST);
            });
            insertSymbol.add(brackets.toString());
        }
        return insertSymbol.toString();
    }

}
