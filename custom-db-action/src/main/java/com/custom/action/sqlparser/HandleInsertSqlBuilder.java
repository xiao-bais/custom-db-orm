package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.fieldfill.ColumnAutoFillHandleUtils;
import com.custom.action.util.DbUtil;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.ConvertUtil;
import com.custom.comm.utils.RexUtil;

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
     * 插入的sql前缀
     * <br/> insert into 表(字段) values
     */
    private final String insertPrefix;

    /**
     * 插入的sql后缀
     * <br/> (?,?,?,?,?)
     */
    private final StringJoiner insertSuffix;

    @Override
    public String createTargetSql() {

        StringJoiner insertSqlField = new StringJoiner(Constants.SEPARATOR_COMMA_1);
        List<T> saveList = getEntityList();
        Asserts.notEmpty(saveList, "未找到待新增的数据");

        if (saveList.size() == 1) {
            this.extractParams(saveList.get(0));
            return this.insertPrefix + insertSuffix;
        }

        saveList.forEach(op -> {
            this.extractParams(op);
            insertSqlField.add(insertSuffix.toString());
        });
        return insertPrefix + insertSqlField;
    }


    /**
     * 获取字段的值
     */
    private Object getFieldValue(DbFieldParserModel<T> fieldModel) {
        Object fieldValue = fieldModel.getValue();
        try {
            // 若存在自动填充的字段，则在添加的时候，进行字段值的自动填充
            if (ColumnAutoFillHandleUtils.exists(getEntityClass(), fieldModel.getFieldName())
                    && Objects.isNull(fieldValue) ) {
                fieldValue = ColumnAutoFillHandleUtils.getFillValue(getEntityClass(), fieldModel.getFieldName());
                fieldModel.setValue(fieldValue);
            }

            // 否则若该字段为逻辑删除字段，则填入未删除的默认值
            else if (checkLogicFieldIsExist()
                    && fieldModel.getColumn().equals(getLogicColumn())) {
                fieldValue = ConvertUtil.transToObject(fieldModel.getType(), getLogicNotDeleteValue());
                fieldModel.setValue(fieldValue);
            }

        } catch (Exception e) {
            fieldValue = ConvertUtil.transToObject(fieldModel.getType(),
                    RexUtil.regexStr(RexUtil.single_quotes, String.valueOf(getLogicNotDeleteValue()))
            );
        }

        return fieldValue;
    }


    public HandleInsertSqlBuilder(Class<T> entityClass) {
        this.insertSuffix = new StringJoiner(Constants.SEPARATOR_COMMA_1,
                Constants.BRACKETS_LEFT, Constants.BRACKETS_RIGHT
        );

        // 添加?
        TableParseModel<T> tableSqlBuilder = TableInfoCache.getTableModel(entityClass);
        // 初始化
        this.injectTableInfo(tableSqlBuilder);

        if (tableSqlBuilder.getKeyParserModel() != null) {
            this.insertSuffix.add(Constants.QUEST);
        }
        int fieldSize = tableSqlBuilder.getFieldParserModels().size();
        for (int i = 0; i < fieldSize; i++) {
            this.insertSuffix.add(Constants.QUEST);
        }

        // 添加字段
        StringJoiner insertColumn = new StringJoiner(Constants.SEPARATOR_COMMA_2);
        if (Objects.nonNull(getKeyParserModel())) {
            insertColumn.add(getKeyParserModel().getDbKey());
        }
        if (!getFieldParserModels().isEmpty()) {
            getFieldParserModels().forEach(x -> insertColumn.add(x.getColumn()));
        }
        this.insertPrefix = String.format(DbUtil.INSERT_TEMPLATE, getTable(), insertColumn);


    }

    /**
     * 提取字段值
     * @param currEntity
     */
    private void extractParams(T currEntity) {
        Asserts.npe(currEntity);
        setEntity(currEntity);
        DbKeyParserModel<T> keyParserModel = getKeyParserModel();

        // 读取
        if (keyParserModel != null) {
            Object keyValue = keyParserModel.generateKey();
            this.addParams(keyValue);
        }
        List<DbFieldParserModel<T>> fieldParserModels = getFieldParserModels();
        for (DbFieldParserModel<T> model : fieldParserModels) {
            Object fieldValue = getFieldValue(model);
            this.addParams(fieldValue);
        }
    }

}
