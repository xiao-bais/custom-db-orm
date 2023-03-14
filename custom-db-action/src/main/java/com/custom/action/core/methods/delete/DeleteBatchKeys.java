package com.custom.action.core.methods.delete;

import com.custom.action.core.TableInfoCache;
import com.custom.action.core.methods.MethodKind;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Xiao-Bai
 * @since 2023/3/13 14:01
 */
@SuppressWarnings("unchecked")
public class DeleteBatchKeys extends DeleteByCondition {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getDeleteSqlBuilderCache(target, executorFactory);
        Collection<? extends Serializable> keys = (Collection<? extends Serializable>) params[1];
        String condition = sqlBuilder.createKeysCondition(keys);
        return super.createSqlSession(executorFactory, target, new Object[]{target, condition, keys.toArray()});
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.DELETE_BATCH_KEYS;
    }
}
