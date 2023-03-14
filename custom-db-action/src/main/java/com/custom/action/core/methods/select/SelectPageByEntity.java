package com.custom.action.core.methods.select;

import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executor.JdbcExecutorFactory;

/**
 * @author Xiao-Bai
 * @since 2023/3/11 23:22
 */
public class SelectPageByEntity extends SelectPageByWrapper {

    @Override
    public <T> Object doExecute(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        return super.doExecute(executorFactory, target, params);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_PAGE_BY_ENTITY;
    }
}
