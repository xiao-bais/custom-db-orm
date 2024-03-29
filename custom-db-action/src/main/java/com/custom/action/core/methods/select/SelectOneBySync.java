package com.custom.action.core.methods.select;

import com.custom.action.core.methods.MethodKind;
import com.custom.action.core.syncquery.SyncQueryWrapper;
import com.custom.jdbc.session.JdbcSqlSessionFactory;

import java.util.Collections;

/**
 * @author Xiao-Bai
 * @since 2023/3/27 14:56
 */
@SuppressWarnings("unchecked")
public class SelectOneBySync extends SelectOneByWrapper {

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        SyncQueryWrapper<T> queryWrapper = (SyncQueryWrapper<T>) params[0];
        Object primaryResult = super.doExecute(sqlSessionFactory, target, new Object[]{queryWrapper.getPrimaryWrapper()});

        if (primaryResult != null || queryWrapper.getSyncPropertyList() != null) {
            super.resultPropertyInject(sqlSessionFactory, target, queryWrapper, Collections.singletonList((T) primaryResult));
        }
        return primaryResult;
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_ONE_BY_SYNC;
    }
}
