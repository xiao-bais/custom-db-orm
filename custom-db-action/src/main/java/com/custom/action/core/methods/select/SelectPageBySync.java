package com.custom.action.core.methods.select;

import com.custom.action.core.methods.MethodKind;
import com.custom.action.core.syncquery.SyncQueryWrapper;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.session.JdbcSqlSessionFactory;

/**
 * @author Xiao-Bai
 * @since 2023/3/27 14:56
 */
@SuppressWarnings("unchecked")
public class SelectPageBySync extends SelectPageByWrapper {

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        SyncQueryWrapper<T> queryWrapper = (SyncQueryWrapper<T>) params[0];
        Object primaryResult = super.doExecute(sqlSessionFactory, target, new Object[]{queryWrapper.getPrimaryWrapper()});

        if (primaryResult != null || queryWrapper.getSyncPropertyList() != null) {
            DbPageRows<T> pageRows = (DbPageRows<T>) primaryResult;
            super.resultPropertyInject(sqlSessionFactory, target, queryWrapper,  pageRows.getData());
        }
        return primaryResult;
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_PAGE_BY_SYNC;
    }
}
