package com.custom.action.core.methods.delete;

import com.custom.action.core.TableInfoCache;
import com.custom.action.core.methods.MethodKind;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
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
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        AbstractSqlBuilder<T> sqlBuilder = super.getDeleteSqlBuilder(sqlSessionFactory, target);
        Collection<? extends Serializable> keys = (Collection<? extends Serializable>) params[1];
        String condition = sqlBuilder.createKeysCondition(keys);
        return super.createSqlSession(sqlSessionFactory, target, new Object[]{target, condition, keys.toArray()});
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.DELETE_BATCH_KEYS;
    }
}
