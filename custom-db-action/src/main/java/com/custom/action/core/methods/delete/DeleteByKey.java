package com.custom.action.core.methods.delete;

import com.custom.action.core.TableInfoCache;
import com.custom.action.core.methods.MethodKind;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.jdbc.executor.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

import java.io.Serializable;

/**
 * @author Xiao-Bai
 * @since 2023/3/13 13:47
 */
public class DeleteByKey extends DeleteByCondition {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getDeleteSqlBuilderCache(target, sqlSessionFactory);
        Serializable key = (Serializable) params[1];
        String condition = sqlBuilder.createKeyCondition(key);
        return super.createSqlSession(sqlSessionFactory, target, new Object[]{target, condition, key});
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.DELETE_BY_KEY;
    }
}
