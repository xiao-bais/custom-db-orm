package com.custom.jdbc.dbAdapetr;

import com.custom.comm.utils.ConvertUtil;
import com.custom.jdbc.condition.BaseExecutorBody;
import com.custom.jdbc.condition.SelectExecutorBody;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.executor.CustomJdbcExecutor;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.DatabaseAdapter;
import com.custom.jdbc.interfaces.SqlSessionExecutor;
import com.custom.jdbc.session.CustomSqlSession;
import com.custom.jdbc.transaction.DbConnGlobal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

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
