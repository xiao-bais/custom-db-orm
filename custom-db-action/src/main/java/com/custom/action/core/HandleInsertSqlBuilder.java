package com.custom.action.core;

import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.comm.enums.FillStrategy;
import com.custom.comm.enums.SqlExecTemplate;
import com.custom.comm.utils.AssertUtil;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.ConvertUtil;
import com.custom.comm.utils.RexUtil;
import com.custom.jdbc.session.JdbcSqlSessionFactory;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * 提供一系列新增记录的sql构建
 * @author   Xiao-Bai
 * @since  2022/4/3 17:26
 */
@SuppressWarnings("unchecked")
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
        throw new UnsupportedOperationException();
    }

    @Override
    public String createTargetSql(boolean primaryTable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String createTargetSql(Object obj, List<Object> sqlParams) {

        if (obj instanceof List) {
            List<T> saveList = (List<T>) obj;
            StringJoiner insertSqlField = new StringJoiner(Constants.SEPARATOR_COMMA_2);

            AssertUtil.notEmpty(saveList, "No data to operate.");

            if (saveList.size() == 1) {
                this.extractParams(saveList.get(0), sqlParams);
                return this.insertPrefix + insertSuffix;
            }

            saveList.forEach(op -> {
                AssertUtil.npe(op, "Null object in saveList.");
                this.extractParams(op, sqlParams);
                insertSqlField.add(insertSuffix.toString());
            });
            return insertPrefix + insertSqlField;
        }
        return "";
    }


    /**
     * 获取字段的值
     */
    private Object getFieldValue(DbFieldParserModel<T> fieldModel, T entity) {
        Object fieldValue = fieldModel.getValue(entity);
        if (fieldValue != null) {
            return fieldValue;
        }
        try {
            // 若存在自动填充的字段，则在添加的时候，进行字段值的自动填充
            if (this.existFill() && fieldModel.getFillStrategy() != FillStrategy.DEFAULT
                    && !fieldModel.getColumn().equals(getLogicColumn())) {
                fieldValue = this.findFillValue(fieldModel.getFieldName(), fieldModel.getType(), FillStrategy.INSERT);
                if (fieldValue != null) {
                    fieldModel.setValue(entity, fieldValue);
                }
            }

            // 否则若该字段为逻辑删除字段，则填入未删除的默认值
            else if (checkLogicFieldIsExist()
                    && fieldModel.getColumn().equals(getLogicColumn())) {
                fieldValue = ConvertUtil.transToObject(fieldModel.getType(), getLogicNotDeleteValue());
                fieldModel.setValue(entity, fieldValue);
            }

        } catch (Exception e) {
            fieldValue = ConvertUtil.transToObject(fieldModel.getType(),
                    RexUtil.regexStr(RexUtil.single_quotes, String.valueOf(getLogicNotDeleteValue()))
            );
        }

        return fieldValue;
    }


    public HandleInsertSqlBuilder(Class<T> entityClass, JdbcSqlSessionFactory sqlSessionFactory) {
        this.insertSuffix = new StringJoiner(Constants.SEPARATOR_COMMA_1,
                Constants.BRACKETS_LEFT, Constants.BRACKETS_RIGHT
        );

        // 添加?
        TableParseModel<T> tableSqlBuilder = TableInfoCache.getTableModel(entityClass);
        // 初始化
        this.injectTableInfo(tableSqlBuilder, sqlSessionFactory);

        if (tableSqlBuilder.getKeyParserModel() != null) {
            this.insertSuffix.add(Constants.QUEST);
        }
        int fieldSize = getFieldParserModels().size();
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

        this.insertPrefix = SqlExecTemplate.format(SqlExecTemplate.INSERT_DATA, getTable(), insertColumn);

    }

    /**
     * 提取字段值
     * @param currEntity
     */
    private void extractParams(T currEntity, List<Object> sqlParams) {
        AssertUtil.npe(currEntity);
        DbKeyParserModel<T> keyParserModel = getKeyParserModel();

        // 读取
        if (keyParserModel != null) {
            Object keyValue = keyParserModel.generateKey(currEntity);
            this.addParams(keyValue, sqlParams);
        }
        List<DbFieldParserModel<T>> fieldParserModels = getFieldParserModels();
        for (DbFieldParserModel<T> model : fieldParserModels) {
            Object fieldValue = getFieldValue(model, currEntity);
            this.addParams(fieldValue, sqlParams);
        }
    }

}
