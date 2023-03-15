package com.custom.jdbc.dbAdapetr;

import com.custom.comm.utils.ConvertUtil;
import com.custom.jdbc.executebody.SelectExecutorBody;
import com.custom.jdbc.executor.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;
import com.custom.jdbc.interfaces.DatabaseAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author  Xiao-Bai
 * @since  2022/10/29 0029 12:14
 */
public abstract class AbstractDbAdapter implements DatabaseAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected boolean queryBoolean(String selectSql) {
        Object res;
        try {
            SelectExecutorBody<Object> paramInfo = new SelectExecutorBody<>(Object.class, selectSql, false, new Object[]{});
            CustomSqlSession sqlSession = executorFactory.createSqlSession(paramInfo);
            res = executorFactory.getJdbcExecutor().selectObj(sqlSession);
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return false;
        }
        return ConvertUtil.conBool(res);
    }


    private final JdbcSqlSessionFactory executorFactory;

    public AbstractDbAdapter(JdbcSqlSessionFactory executorFactory) {
        this.executorFactory = executorFactory;
    }

    public JdbcSqlSessionFactory getExecutorFactory() {
        return executorFactory;
    }
}
