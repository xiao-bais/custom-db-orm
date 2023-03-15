package com.custom.action.core;

import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.util.DbUtil;
import com.custom.comm.enums.FillStrategy;
import com.custom.comm.enums.SqlExecTemplate;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.StrUtils;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 提供一系列删除记录的sql构建
 * @author   Xiao-Bai
 * @since  2022/4/10 15:07
 */
public class HandleDeleteSqlBuilder<T> extends AbstractSqlBuilder<T> {

    private static final Logger logger = LoggerFactory.getLogger(HandleDeleteSqlBuilder.class);

    @Override
    public String createTargetSql() {
        String deleteSql = "";
        try {
            boolean isExist = checkLogicFieldIsExist();
            if (isExist) {
                String setColumnSql = getLogicDeleteUpdateSql();
                String customFillSql = this.handleLogicDelBefore();

                if (StrUtils.isNotBlank(customFillSql)) {
                    setColumnSql = setColumnSql + Constants.SEPARATOR_COMMA_2 + customFillSql;
                }

                // 逻辑删除
                deleteSql = SqlExecTemplate.format(SqlExecTemplate.LOGIC_DELETE, getTable(), getAlias(), setColumnSql);

            } else {

                // 物理删除
                deleteSql = SqlExecTemplate.format(SqlExecTemplate.DELETE_DATA, getTable(), getAlias());
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return deleteSql;
    }

    @Override
    public String createTargetSql(boolean primaryTable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String createTargetSql(Object obj, List<Object> sqlParams) {
        throw new UnsupportedOperationException();
    }


    /**
     * 在删除数据时，若是有逻辑删除，则在逻辑删除前，进行固定字段的自动填充
     * <br/> 例如: 修改时间，修改人
     */
    protected String handleLogicDelBefore() {

        if (!this.existFill()) {
            return Constants.EMPTY;
        }
        List<DbFieldParserModel<T>> fieldParserModels = getFieldParserModels().stream()
                .filter(e -> e.getFillStrategy() != FillStrategy.DEFAULT)
                .collect(Collectors.toList());
        StringJoiner autoUpdateFieldSql = new StringJoiner(Constants.SEPARATOR_COMMA_2);
        for (DbFieldParserModel<T> filedInfo : fieldParserModels) {
            Object fieldVal = this.findFillValue(filedInfo.getFieldName(), filedInfo.getType(), FillStrategy.UPDATE);
            if (fieldVal != null) {
                String updateSetField = DbUtil.formatMapperSqlCondition(filedInfo.getFieldSql(), fieldVal);
                autoUpdateFieldSql.add(updateSetField);
            }
        }
        return autoUpdateFieldSql.toString();
    }

    public HandleDeleteSqlBuilder(Class<T> entityClass, JdbcSqlSessionFactory sqlSessionFactory) {
        TableParseModel<T> tableSqlBuilder = TableInfoCache.getTableModel(entityClass);
        this.injectTableInfo(tableSqlBuilder, sqlSessionFactory);
    }
}
