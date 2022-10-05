package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.fieldfill.ColumnAutoFillHandleUtils;
import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.action.util.DbUtil;
import com.custom.action.condition.SFunction;
import com.custom.comm.Asserts;
import com.custom.comm.JudgeUtil;
import com.custom.comm.Constants;
import com.custom.comm.exceptions.ExThrowsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Xiao-Bai
 * @date 2022/4/10 15:01
 * @desc:构建修改sql
 */
public class HandleUpdateSqlBuilder<T> extends AbstractSqlBuilder<T> {

    private static final Logger logger = LoggerFactory.getLogger(HandleUpdateSqlBuilder.class);

    /**
     * 修改的字段 set部分的sql
     */
    private final StringJoiner updateSqlColumns;

    public HandleUpdateSqlBuilder(Class<T> entityClass) {
        updateSqlColumns = new StringJoiner(Constants.SEPARATOR_COMMA_2);
        TableParseModel<T> tableSqlBuilder = TableInfoCache.getTableModel(entityClass);
        this.injectTableInfo(tableSqlBuilder);
    }

    @SuppressWarnings("unchecked")
    public HandleUpdateSqlBuilder(T entity) {
        this((Class<T>) entity.getClass());
    }


    /**
     * 构建修改的sql字段语句(条件构造器使用)
     */
    @Override
    public String createTargetSql() {
        this.createUpdateSetColumn();
        Asserts.notEmpty(updateSqlColumns, "update set column segment cannot be empty");

        return String.format(DbUtil.UPDATE_TEMPLATE, getTable(), getAlias(),
                updateSqlColumns, Constants.EMPTY);
    }


    /**
     * 修改字段构建（set之后 where之前）
     */
    private void createUpdateSetColumn() {
        for (DbFieldParserModel<T> field : getFieldParserModels()) {
            Object value = field.getValue();
            if (Objects.isNull(value)) {
                // 修改时必要的自动填充
                // 当修改时，用户没有为自动填充的字段额外设置业务值，则启用原本设定的默认值进行填充
                Object fillValue = ColumnAutoFillHandleUtils
                        .getFillValue(getEntityClass(), field.getFieldName());
                if (Objects.nonNull(fillValue)) {
                    value = fillValue;
                }
            }
            if (Objects.nonNull(value)) {
                updateSqlColumns.add(DbUtil.formatSqlCondition(field.getFieldSql()));
                this.addParams(value);
            }
        }
    }

}
