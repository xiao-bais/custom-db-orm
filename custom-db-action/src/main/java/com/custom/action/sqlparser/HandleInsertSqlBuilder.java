package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.fieldfill.ColumnAutoFillHandleUtils;
import com.custom.action.util.DbUtil;
import com.custom.comm.ConvertUtil;
import com.custom.comm.JudgeUtil;
import com.custom.comm.RexUtil;
import com.custom.comm.SymbolConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author Xiao-Bai
 * @date 2022/4/3 17:26
 * @desc:提供一系列新增记录的sql构建
 */
public class HandleInsertSqlBuilder<T> extends AbstractSqlBuilder<T> {

    /**
     * 批量插入时，若要插入的记录数超过该值，则开启分批插入，则该值为每一批插入的最大记录数
     */
    private int saveSubSelection = 1000;
    /**
     * 分段插入次数
     */
    private int subCount = 1;
    /**
     * 当前插入的次数
     */
    private int currentSubIndex= 1;
    /**
     * 是否存在需要分批插入的情况
     */
    private boolean hasSubSelect = false;

    /**
     * 本次插入的总记录数
     */
    private int saveSize = 0;

    /**
     * 本次分段插入的数据
     */
    private List<T> subList;
    /**
     * 插入的sql前缀
     */
    private String insertPrefix = "";

    @Override
    public String createTargetSql() {
        StringJoiner insertColumn = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
        if (Objects.nonNull(getKeyParserModel())) {
            insertColumn.add(getKeyParserModel().getDbKey());
        }
        if (!getFieldParserModels().isEmpty()) {
            getFieldParserModels().forEach(x -> insertColumn.add(x.getColumn()));
        }
        if (JudgeUtil.isEmpty(this.insertPrefix)) {
            this.insertPrefix = String.format(DbUtil.INSERT_TEMPLATE, getTable(), insertColumn);
        }
        String targetSql = insertPrefix + (this.hasSubSelect ?  forBuildBatchSaveInfoBySubSql() : forBuildSaveInfoBySubSql());
        setEntity(null);
        setEntityList(new ArrayList<>());
        return targetSql;
    }

    /**
     * 数据初始化
     */
    protected void dataInitialize() {
        this.saveSize = getEntityList().size();
        if (saveSize > saveSubSelection) {
            this.hasSubSelect = true;
        }
        this.subCount = saveSize % 1000  == 0 ? saveSize / 1000 : saveSize / 1000 + 1;
    }


    /**
     * 批量插入的sql
     */
    private String forBuildBatchSaveInfoBySubSql() {
        int endRows = Math.min(this.currentSubIndex * this.saveSubSelection, this.saveSize);
        if (this.currentSubIndex < subCount) {
            this.subList = getEntityList().subList((this.currentSubIndex - 1) * this.saveSubSelection, this.currentSubIndex * this.saveSubSelection);
            appendSymbol(this.subList);
        } else {
            this.subList = getEntityList().subList((this.currentSubIndex - 1) * this.saveSubSelection, endRows);
        }
        this.currentSubIndex ++;
        return appendSymbol(this.subList);
    }



    /**
     * 单次插入
     */
    protected String forBuildSaveInfoBySubSql() {
        return appendSymbol(getEntityList());
    }

    /**
     * 动态拼接插入的？
     */
    private String appendSymbol(List<T> saveDataList) {
        StringJoiner insertSymbol = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_1);
        for (T currEntity : saveDataList) {
            setEntity(currEntity);
            StringJoiner brackets = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_1, SymbolConstant.BRACKETS_LEFT, SymbolConstant.BRACKETS_RIGHT);
            if (Objects.nonNull(getKeyParserModel())) {
                brackets.add(SymbolConstant.QUEST);
                this.addParams(getKeyParserModel().generateKey());
            }
            List<DbFieldParserModel<T>> fieldParserModels = getFieldParserModels();
            fieldParserModels.forEach(x -> {
                Object fieldValue = getFieldValue(x);
                x.setValue(fieldValue);
                this.addParams(fieldValue);
                brackets.add(SymbolConstant.QUEST);
            });
            insertSymbol.add(brackets.toString());
        }
        return insertSymbol.toString();
    }

    /**
     * 获取字段的值
     */
    private Object getFieldValue(DbFieldParserModel<T> x) {
        Object fieldValue = x.getValue();
        try {
            // 若存在自动填充的字段，则在添加的时候，进行字段值的自动填充
            if (ColumnAutoFillHandleUtils.exists(getEntityClass(), x.getFieldName())
                    && Objects.isNull(fieldValue) ) {
                fieldValue = ColumnAutoFillHandleUtils.getFillValue(getEntityClass(), x.getFieldName());
            }

            // 否则若该字段为逻辑删除字段，则填入未删除的默认值
            else if (checkLogicFieldIsExist()
                    && x.getColumn().equals(getLogicColumn())) {
                fieldValue = ConvertUtil.transToObject(x.getType(), getLogicNotDeleteValue());
            }

        } catch (Exception e) {
            fieldValue = ConvertUtil.transToObject(x.getType(),
                    RexUtil.regexStr(RexUtil.single_quotes, String.valueOf(getLogicNotDeleteValue()))
            );
        }
        // 当设定了默认值时，若字段值为null，则可从用户自定义或给定的默认值中设入
        if (JudgeUtil.isEmpty(fieldValue)
                && Objects.nonNull(x.getDefaultValue())) {
            fieldValue = x.getDefaultValue();
        }

        return fieldValue;
    }

    public void setSaveSubSelection(int saveSubSelection) {
        this.saveSubSelection = saveSubSelection;
    }

    public boolean isHasSubSelect() {
        return hasSubSelect;
    }

    public int getSubCount() {
        return subCount;
    }

    public List<T> getSubList() {
        return subList;
    }

    public HandleInsertSqlBuilder() {
    }

    public HandleInsertSqlBuilder(Class<T> entityClass) {
        TableSqlBuilder<T> tableSqlBuilder = TableInfoCache.getTableModel(entityClass);
        this.injectTableInfo(tableSqlBuilder);
    }
}
