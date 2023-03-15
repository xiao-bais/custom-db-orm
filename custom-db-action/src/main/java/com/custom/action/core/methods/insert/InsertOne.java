package com.custom.action.core.methods.insert;

import com.custom.action.core.DbKeyParserModel;
import com.custom.action.core.TableInfoCache;
import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.comm.utils.AssertUtil;
import com.custom.jdbc.executebody.SaveExecutorBody;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Xiao-Bai
 * @since 2023/3/13 13:31
 */
@SuppressWarnings("unchecked")
public class InsertOne extends AbstractMethod {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getInsertSqlBuilderCache(target, executorFactory);

        List<T> list;
        if (params[0] instanceof Collection) {
            list = new ArrayList<>(((Collection<T>) params[0]));
        }else {
            T entity = (T) params[0];
            list = Collections.singletonList(entity);
        }
        AssertUtil.notEmpty(list, "insert data cannot be empty ");

        List<Object> sqlParamList = new ArrayList<>();
        String insertSql = sqlBuilder.createTargetSql(list, sqlParamList);
        DbKeyParserModel<T> keyParserModel = sqlBuilder.getKeyParserModel();
        SaveExecutorBody<T> paramInfo = new SaveExecutorBody<>(
                list,
                keyParserModel.getField(),
                insertSql,
                sqlPrintSupport,
                sqlParamList.toArray()
        );
        return executorFactory.createSqlSession(paramInfo);
    }

    @Override
    public <T> Object doExecute(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = createSqlSession(executorFactory, target, params);
        return executorFactory.getJdbcExecutor().executeSave(sqlSession);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.INSERT_ONE;
    }
}
