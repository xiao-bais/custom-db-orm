package com.custom.action.core.methods.insert;

import com.custom.action.core.DbKeyParserModel;
import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.comm.utils.AssertUtil;
import com.custom.jdbc.executebody.ExecuteBodyHelper;
import com.custom.jdbc.executebody.SaveExecutorBody;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
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
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        AbstractSqlBuilder<T> sqlBuilder = super.getInsertSqlBuilder(sqlSessionFactory, target);

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
        SaveExecutorBody<T> paramInfo = ExecuteBodyHelper.createSave(
                list,
                keyParserModel.getField(),
                insertSql,
                sqlPrintSupport,
                sqlParamList.toArray()
        );
        return sqlSessionFactory.createSqlSession(paramInfo);
    }

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = createSqlSession(sqlSessionFactory, target, params);
        return sqlSessionFactory.getJdbcExecutor().executeSave(sqlSession);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.INSERT_ONE;
    }
}
