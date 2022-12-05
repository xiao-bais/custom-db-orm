package com.custom.action.core;

import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.fieldfill.ColumnAutoFillHandleUtils;
import com.custom.action.util.DbUtil;
import com.custom.comm.enums.SqlExecTemplate;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.Constants;
import com.custom.jdbc.executor.JdbcExecutorFactory;
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
    private StringJoiner updateSqlColumns;



    public HandleUpdateSqlBuilder(Class<T> entityClass, JdbcExecutorFactory executorFactory) {
        updateSqlColumns = new StringJoiner(Constants.SEPARATOR_COMMA_2);
        TableParseModel<T> tableSqlBuilder = TableInfoCache.getTableModel(entityClass);
        this.injectTableInfo(tableSqlBuilder, executorFactory);
    }


    /**
     * 构建修改的sql字段语句(条件构造器使用)
     */
    @Override
    public String createTargetSql() {
        return createTargetSql(false);
    }

    @Override
    public String createTargetSql(Object obj) {

        // 在执行修改时，是否允许添加null值的字段
         boolean addNullField = (Boolean) obj;

        // 创建需要set的sql字段
        this.createUpdateSetColumn(addNullField);
        Asserts.notEmpty(updateSqlColumns, "update set column segment cannot be empty");

        return SqlExecTemplate.format(SqlExecTemplate.UPDATE_DATA, getTable(), getAlias(),
                updateSqlColumns, Constants.EMPTY);
    }

    @Override
    public void clear() {
        this.updateSqlColumns = new StringJoiner(Constants.SEPARATOR_COMMA_2);
        super.clear();
    }


    /**
     * 修改字段构建（set之后 where之前）
     * @param addNullField 是否允许添加=null的字段
     */
    private void createUpdateSetColumn(boolean addNullField) {
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
            if (Objects.nonNull(value) || addNullField) {
                updateSqlColumns.add(DbUtil.formatSqlCondition(field.getFieldSql()));
                this.addParams(value);
            }
        }
    }
}