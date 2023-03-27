package com.custom.action.core.methods;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.TableInfoCache;
import com.custom.action.core.TableParseModel;
import com.custom.action.core.methods.select.SelectListByWrapper;
import com.custom.action.core.methods.select.SelectMapByWrapper;
import com.custom.action.core.methods.select.SelectObjByWrapper;
import com.custom.action.core.methods.select.SelectOneByWrapper;
import com.custom.action.core.syncquery.SyncFunction;
import com.custom.action.core.syncquery.SyncProperty;
import com.custom.action.core.syncquery.SyncQueryWrapper;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.interfaces.ExecuteHandler;
import com.custom.comm.enums.SqlExecTemplate;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.page.DbPageRows;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.StrUtils;
import com.custom.comm.utils.lambda.LambdaUtil;
import com.custom.comm.utils.lambda.TargetSetter;
import com.custom.jdbc.executebody.ExecuteBodyHelper;
import com.custom.jdbc.executebody.SelectExecutorBody;
import com.custom.jdbc.interfaces.CustomSqlSession;
import com.custom.jdbc.interfaces.DatabaseAdapter;
import com.custom.jdbc.session.JdbcSqlSessionFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author Xiao-Bai
 * @since 2023/3/8 19:02
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMethod implements ExecuteHandler {

    protected static boolean sqlPrintSupport = true;

    protected abstract <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory,
                                                             Class<T> target,
                                                             Object[] params) throws Exception;


    public <T> Class<T> getMappedType(Object[] params) {
        return getMappedType(params, 0);
    }


    protected <T> Class<T> getMappedType(Object[] params, int index) {
        if (params.length == 0 || params[index] == null) {
            throw new NullPointerException();
        }
        Object param = params[index];
        if (param instanceof CharSequence) {
            return (Class<T>) String.class;
        }
        if (param instanceof ConditionWrapper) {
            ConditionWrapper<T> conditionWrapper = (ConditionWrapper<T>) param;
            return conditionWrapper.getEntityClass();
        }
        if (param instanceof SyncQueryWrapper) {
            SyncQueryWrapper<T> queryWrapper = (SyncQueryWrapper<T>) params[0];
            return queryWrapper.getEntityClass();
        }
        if (param instanceof AbstractUpdateSet) {
            AbstractUpdateSet<T> updateSet = (AbstractUpdateSet<T>) param;
            return updateSet.thisEntityClass();
        }
        return param instanceof Class ?
                (Class<T>) param : (Class<T>) param.getClass();
    }


    protected <T> AbstractSqlBuilder<T> getSelectSqlBuilder(JdbcSqlSessionFactory sqlSessionFactory, Class<T> mappedType) {
        return TableInfoCache.getSelectSqlBuilderCache(mappedType, sqlSessionFactory);
    }

    protected <T> AbstractSqlBuilder<T> getInsertSqlBuilder(JdbcSqlSessionFactory sqlSessionFactory, Class<T> mappedType) {
        return TableInfoCache.getInsertSqlBuilderCache(mappedType, sqlSessionFactory);
    }

    protected <T> AbstractSqlBuilder<T> getDeleteSqlBuilder(JdbcSqlSessionFactory sqlSessionFactory, Class<T> mappedType) {
        return TableInfoCache.getDeleteSqlBuilderCache(mappedType, sqlSessionFactory);
    }

    protected <T> AbstractSqlBuilder<T> getUpdateSqlBuilder(JdbcSqlSessionFactory sqlSessionFactory, Class<T> mappedType) {
        return TableInfoCache.getUpdateSqlBuilderCache(mappedType, sqlSessionFactory);
    }

    protected <T> AbstractSqlBuilder<T> getEmptySqlBuilder(JdbcSqlSessionFactory sqlSessionFactory, Class<T> mappedType) {
        return TableInfoCache.getEmptySqlBuilder(mappedType, sqlSessionFactory);
    }

    /**
     * 分页数据整合
     */
    protected <T> void buildPageResult(JdbcSqlSessionFactory sqlSessionFactory,
                                       Class<T> target,
                                       String selectSql,
                                       DbPageRows<T> dbPageRows,
                                       Object[] params) throws Exception {

        List<T> dataList = new ArrayList<>();

        CustomSqlSession countSqlSession = this.createCountSqlSession(sqlSessionFactory, selectSql, params);
        long count = (long) sqlSessionFactory.getJdbcExecutor().selectObj(countSqlSession);

        if (count > 0) {
            CustomSqlSession selectSqlSession = this.createPageSqlSession(sqlSessionFactory, target, selectSql, dbPageRows.getPageIndex(), dbPageRows.getPageSize(), params);
            dataList = sqlSessionFactory.getJdbcExecutor().selectList(selectSqlSession);
        }
        dbPageRows.setTotal(count).setData(dataList);
    }

    protected CustomSqlSession createCountSqlSession(JdbcSqlSessionFactory sqlSessionFactory, String selectSql, Object[] params) {
        // 格式化并获取selectCountSQL
        String selectCountSql = SqlExecTemplate.format(SqlExecTemplate.SELECT_COUNT, selectSql);
        SelectExecutorBody<Long> executorBody = ExecuteBodyHelper.createSelectIf(
                Long.class,
                selectCountSql,
                sqlPrintSupport,
                params
        );
        return sqlSessionFactory.createSqlSession(executorBody);
    }

    protected <T> CustomSqlSession createPageSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, String selectSql, int pageIndex, int pageSize, Object[] params) {
        DatabaseAdapter databaseAdapter = sqlSessionFactory.getDatabaseAdapter();
        selectSql = databaseAdapter.handlePage(selectSql, pageIndex, pageSize);
        SelectExecutorBody<T> selectExecutorBody = ExecuteBodyHelper.createSelectIf(
                target,
                selectSql,
                sqlPrintSupport,
                params
        );
        return sqlSessionFactory.createSqlSession(selectExecutorBody);
    }


    /**
     * 结果集对象中剩余属性的值写入
     */
    protected <T, P> void resultPropertyInject(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target,
                                               SyncQueryWrapper<T> queryWrapper,
                                               Collection<T> resultList) throws Exception {
        for (T data : resultList) {
            if (data == null) {
                continue;
            }
            List<SyncProperty<T, ?>> syncProperties = queryWrapper.getSyncProperties();
            for (SyncProperty<T, ?> property : syncProperties) {
                Predicate<T> ifCondition = property.getCondition();
                if (ifCondition == null || !ifCondition.test(data)) {
                    continue;
                }
                TargetSetter<T, P> setter = (TargetSetter<T, P>) property.getSetter();
                ConditionWrapper<?> wrapper = property.getWrapper();
                SyncFunction<T, ?> syncFunction = property.getSyncFunction();
                if (setter == null || (wrapper == null && syncFunction == null)) {
                    continue;
                }
                if (syncFunction != null) {
                    wrapper = syncFunction.doQuery(data);
                }

                TableParseModel<?> tableModel = TableInfoCache.getTableModel(target);
                String implMethodName = LambdaUtil.getImplMethodName(setter);
                String fieldName = StrUtils.trimSet(implMethodName);

                Field field = tableModel.getFields().stream()
                        .filter(op -> op.getName().equals(fieldName))
                        .findFirst()
                        .orElseThrow(() -> new CustomCheckException("Parse setter method error: " + implMethodName + " in " + target));

                Class<?> fieldType = field.getType();
                if (fieldType.isArray() || fieldType.isEnum()) {
                    throw new UnsupportedOperationException("Injection methods with attribute type of (array/enum) are not currently supported.");
                }
                P result = null;
                ExecuteHandler executeHandler = null;
                Object[] preParamArr = {wrapper};
                if (Collection.class.isAssignableFrom(fieldType)) {
                    executeHandler = new SelectListByWrapper();
                } else if (Map.class.isAssignableFrom(fieldType)) {
                    executeHandler = new SelectMapByWrapper();
                } else if (CustomUtil.isBasicClass(fieldType)) {
                    executeHandler = new SelectObjByWrapper();
                } else {
                    executeHandler = new SelectOneByWrapper();
                }

                Class<T> mappedType = executeHandler.getMappedType(preParamArr);
                result = (P) executeHandler.doExecute(sqlSessionFactory, mappedType, preParamArr);

                if (Set.class.isAssignableFrom(fieldType)) {
                    result = (P) new HashSet<>((Collection<T>) result);
                }

                if (result != null) {
                    setter.accept(data, result);
                }
            }
        }
    }
}
