package com.custom.jdbc.dbAdapetr;

import com.custom.comm.utils.ConvertUtil;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.DatabaseAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Xiao-Bai
 * @date 2022/10/29 0029 12:14
 */
public abstract class AbstractDbAdapter implements DatabaseAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected <T> boolean queryBoolean(String selectSql) {
        Object res;
        try {
            res = executorFactory.selectObjBySql(false, selectSql);
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return false;
        }
        return ConvertUtil.conBool(res);
    }


    private final JdbcExecutorFactory executorFactory;

    public AbstractDbAdapter(JdbcExecutorFactory executorFactory) {
        this.executorFactory = executorFactory;
    }

    public JdbcExecutorFactory getExecutorFactory() {
        return executorFactory;
    }
}
