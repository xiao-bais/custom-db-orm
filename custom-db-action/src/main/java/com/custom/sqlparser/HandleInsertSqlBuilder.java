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

    public HandleInsertSqlBuilder() {
        this.insertSql = new StringBuilder();
    }


    @Override
    public String buildSql() {
        StringJoiner insertColumn = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
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
                }else if(JudgeUtilsAx.isNotEmpty(getLogicColumn()) && TableInfoCache.isExistsLogic(getTable())  && x.getColumn().equals(getLogicColumn())) {
                    fieldValue = ConvertUtil.transToObject(x.getType(), getLogicNotDeleteValue());
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
}
