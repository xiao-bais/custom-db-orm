package com.custom.action.core.methods.update;

import com.custom.action.core.DbKeyParserModel;
import com.custom.action.core.TableInfoCache;
import com.custom.action.core.methods.MethodKind;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

import java.io.Serializable;

/**
 * @author Xiao-Bai
 * @since 2023/3/14 12:10
 */
@SuppressWarnings("unchecked")
public class UpdateByKey extends UpdateByCondition {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        AbstractSqlBuilder<T> sqlBuilder = super.getUpdateSqlBuilder(executorFactory, target);

        T entity = (T) params[0];
        DbKeyParserModel<T> keyParserModel = sqlBuilder.getKeyParserModel();
        if (keyParserModel == null) {
            throw new UnsupportedOperationException("Modification is not supported because the primary key is not found in " + target);
        }
        Serializable value = (Serializable) keyParserModel.getValue(entity);
        String condition = sqlBuilder.createKeyCondition(value);
        return super.createSqlSession(executorFactory, target, new Object[]{entity, condition, value});
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.UPDATE_BY_KEY;
    }
}
