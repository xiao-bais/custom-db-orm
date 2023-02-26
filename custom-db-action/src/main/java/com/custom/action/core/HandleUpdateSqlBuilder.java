package com.custom.action.core;

import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.util.DbUtil;
import com.custom.comm.enums.FillStrategy;
import com.custom.comm.enums.SqlExecTemplate;
import com.custom.comm.utils.AssertUtil;
import com.custom.comm.utils.Constants;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * 提供一系列修改记录的sql构建
 * @author   Xiao-Bai
 * @since  2022/4/10 15:01
 */
@SuppressWarnings("unchecked")
public class HandleUpdateSqlBuilder<T> extends AbstractSqlBuilder<T> {

    private static final Logger logger = LoggerFactory.getLogger(HandleUpdateSqlBuilder.class);


    public HandleUpdateSqlBuilder(Class<T> entityClass, JdbcExecutorFactory executorFactory) {
        TableParseModel<T> tableSqlBuilder = TableInfoCache.getTableModel(entityClass);
        this.injectTableInfo(tableSqlBuilder, executorFactory);
    }


    /**
     * 构建修改的sql字段语句(条件构造器使用)
     */
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
        T currEntity = (T) obj;
        StringJoiner updateSqlColumns = new StringJoiner(Constants.SEPARATOR_COMMA_2);

        // 创建需要set的sql字段
        this.createUpdateSetColumn(currEntity, sqlParams, updateSqlColumns);
        AssertUtil.notEmpty(updateSqlColumns, "update set column segment cannot be empty");

        return SqlExecTemplate.format(SqlExecTemplate.UPDATE_DATA, getTable(), getAlias(),
                updateSqlColumns, Constants.EMPTY);
    }


    /**
     * 修改字段构建（set之后 where之前）
     */
    private void createUpdateSetColumn(T currEntity, List<Object> sqlParams, StringJoiner updateSqlColumns) {
        for (DbFieldParserModel<T> field : getFieldParserModels()) {
            Object value = field.getValue(currEntity);
            if (Objects.isNull(value) && field.getFillStrategy() != FillStrategy.DEFAULT && this.existFill()) {
                // 修改时必要的自动填充
                // 当修改时，用户没有为自动填充的字段额外设置业务值，则启用原本设定的默认值进行填充
                Object fillValue = this.findFillValue(field.getFieldName(), field.getType(), FillStrategy.UPDATE);
                if (Objects.nonNull(fillValue)) {
                    value = fillValue;
                    field.setValue(currEntity, fillValue);
                }
            }
            if (Objects.nonNull(value)) {
                updateSqlColumns.add(DbUtil.formatSqlCondition(field.getFieldSql()));
                this.addParams(value, sqlParams);
            }
        }
    }
}
