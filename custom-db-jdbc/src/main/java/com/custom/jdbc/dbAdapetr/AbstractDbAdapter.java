package com.custom.jdbc.dbAdapetr;

import com.custom.comm.utils.ConvertUtil;
import com.custom.jdbc.condition.BaseExecutorModel;
import com.custom.jdbc.condition.SelectExecutorModel;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.executor.CustomJdbcExecutor;
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
    /**
     * 创建SQL请求
     */
    protected CustomSqlSession createSqlSession(BaseExecutorModel model) {
        SqlSessionExecutor sessionExecutor = (connection) -> new CustomSqlSession(connection, model);
        Connection connection = DbConnGlobal.getCurrentConnection(dbDataSource);
        return sessionExecutor.createSession(connection);
    }

    protected boolean queryBoolean(SelectExecutorModel<Long> selectExecutorModel) {
        CustomSqlSession sqlSession = this.createSqlSession(selectExecutorModel);
        CustomJdbcExecutor jdbcExecutor = getJdbcExecutor();

        Object res;
        try {
            res = jdbcExecutor.selectObj(sqlSession);
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return false;
        }
        return ConvertUtil.conBool(res);
    }


    private final DbDataSource dbDataSource;
    private final CustomJdbcExecutor jdbcExecutor;

    public AbstractDbAdapter(DbDataSource dbDataSource, CustomJdbcExecutor jdbcExecutor) {
        this.dbDataSource = dbDataSource;
        this.jdbcExecutor = jdbcExecutor;
    }

    public DbDataSource getDbDataSource() {
        return dbDataSource;
    }

    public CustomJdbcExecutor getJdbcExecutor() {
        return jdbcExecutor;
    }
}
