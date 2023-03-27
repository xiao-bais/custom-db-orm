package com.custom.action.core.methods.select;

import com.custom.action.core.methods.MethodKind;
import com.custom.action.core.syncquery.SyncQueryWrapper;
import com.custom.jdbc.session.JdbcSqlSessionFactory;

import java.util.*;

/**
 * @author Xiao-Bai
 * @since 2023/3/27 0:28
 */
@SuppressWarnings("unchecked")
public class SelectListBySync extends SelectListByWrapper {

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        SyncQueryWrapper<T> queryWrapper = (SyncQueryWrapper<T>) params[0];
        Object primaryResult = super.doExecute(sqlSessionFactory, target, new Object[]{queryWrapper.getPrimaryWrapper()});

        if (primaryResult != null || queryWrapper.getSyncProperties() != null) {
            if (primaryResult instanceof Collection) {
                super.resultPropertyInject(sqlSessionFactory, target, queryWrapper, (Collection<T>) primaryResult);
            }
        }
        return primaryResult;
    }


    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_LIST_BY_SYNC;
    }

}
