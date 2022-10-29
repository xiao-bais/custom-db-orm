package com.custom.action.dbaction;

import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.action.sqlparser.DbFieldParserModel;
import com.custom.action.sqlparser.DbKeyParserModel;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableParseModel;
import com.custom.action.util.DbUtil;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.comm.utils.*;
import com.custom.jdbc.configuration.CustomConfigHelper;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.DatabaseAdapter;
import com.custom.jdbc.transaction.DbConnGlobal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.IntStream;

/**
 * @author Xiao-Bai
 * @date 2022/4/3 17:33
 * @desc: sql操作模板父类
 */
@SuppressWarnings("unchecked")
public abstract class AbstractSqlBuilder<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSqlBuilder.class);

    private String table;
    private String alias;
    private List<T> entityList;
    private Class<T> entityClass;
    private DbKeyParserModel<T> keyParserModel;
    private List<DbFieldParserModel<T>> fieldParserModels;
    private Map<String, String> fieldMapper;
    private Map<String, String> columnMapper;
    private JdbcExecutorFactory executorFactory;
    private String logicColumn;
    private Object logicNotDeleteValue;
    /**
     * 逻辑删除的查询条件
     */
    private String logicDeleteQuerySql;
    /**
     * 逻辑删除的修改条件
     */
    private String logicDeleteUpdateSql;
    private List<Object> sqlParams = new ArrayList<>();

    /**
     * 创建对应的sql
     */
    public abstract String createTargetSql();

    public String getTable() {
        return table;
    }

    public String getAlias() {
        return alias;
    }

    public void injectEntity(T entity) {
        if (Objects.nonNull(keyParserModel)) {
            keyParserModel.setEntity(entity);
        }
        if (!fieldParserModels.isEmpty()) {
            fieldParserModels.forEach(x -> x.setEntity(entity));
        }
    }

    public List<T> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<T> entityList) {
        this.entityList = entityList;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public DbKeyParserModel<T> getKeyParserModel() {
        return keyParserModel;
    }

    public List<DbFieldParserModel<T>> getFieldParserModels() {
        return fieldParserModels;
    }

    public Map<String, String> getFieldMapper() {
        return fieldMapper;
    }

    public Map<String, String> getColumnMapper() {
        return columnMapper;
    }

    public String getLogicColumn() {
        return logicColumn;
    }

    public Object getLogicNotDeleteValue() {
        return logicNotDeleteValue;
    }

    public String getLogicDeleteQuerySql() {
        return this.logicDeleteQuerySql;
    }

    public String getLogicDeleteUpdateSql() {
        return this.logicDeleteUpdateSql;
    }


    /**
     * 由于部分表可能没有逻辑删除字段，所以在每一次执行时，都需检查该表有没有逻辑删除的字段，以保证sql正常执行
     */
    public boolean checkLogicFieldIsExist() {
        if (CustomUtil.isBlank(logicColumn)) {
            return false;
        }
        int order = executorFactory.getDbDataSource().getOrder();
        Boolean existsLogic = TableInfoCache.isExistsLogic(order, table);
        if (existsLogic != null) {
            return existsLogic;
        }
        DatabaseAdapter databaseAdapter = executorFactory.getDatabaseAdapter();
        boolean conBool = databaseAdapter.existColumn(table, logicColumn);
        TableInfoCache.setTableLogic(order, table, conBool);
        return conBool;
    }


    /**
     * 获取sql参数值列表
     */
    public Object[] getSqlParams() {
        if (Objects.isNull(sqlParams)) {
            return new Object[]{};
        }
        return sqlParams.toArray();
    }

    /**
     * 获取sql参数值列表
     */
    public List<Object> getSqlParamList() {
       return sqlParams;
    }

    /**
     * 添加参数值
     */
    public void addParams(Object val) {
        if (Objects.isNull(sqlParams)) {
            sqlParams = new ArrayList<>();
        }
        if (val instanceof List) {
            this.sqlParams.addAll((List<Object>) val);
        }
        this.sqlParams.add(val);
    }

    /**
     * 注入基础表字段数据
     * @param tableSqlBuilder 表解析对象
     * @param order 数据源序号
     */
    protected void injectTableInfo(TableParseModel<T> tableSqlBuilder, int order) {
        this.table = tableSqlBuilder.getTable();
        this.alias = tableSqlBuilder.getAlias();
        this.keyParserModel = tableSqlBuilder.getKeyParserModel();
        this.fieldParserModels = tableSqlBuilder.getFieldParserModels();
        this.columnMapper = tableSqlBuilder.getColumnMapper();
        this.fieldMapper = tableSqlBuilder.getFieldMapper();
        this.entityClass = tableSqlBuilder.getEntityClass();

        CustomConfigHelper configHelper = DbConnGlobal.getConfigHelper(order);
        Asserts.npe(configHelper, "未找到可用的数据源");
        DbCustomStrategy customStrategy = configHelper.getDbCustomStrategy();

        // 设置逻辑删除字段
        this.logicColumn = customStrategy.getDbFieldDeleteLogic();
        this.initLogic(customStrategy.getDeleteLogicValue(), customStrategy.getNotDeleteLogicValue());

        // 设置jdbc执行对象
        this.executorFactory = new JdbcExecutorFactory(configHelper.getDbDataSource(), customStrategy);

    }

    /**
     * 清空暂存
     */
    public void clear() {
        this.entityList = new ArrayList<>();
        this.sqlParams = new ArrayList<>();
        this.injectEntity(null);
    }

    /**
     * 获取主键的值
     */
    public Object primaryKeyVal() {
        if (keyParserModel == null) {
            return null;
        }
        Object value = keyParserModel.getValue();
        this.clear();
        return value;
    }

    /**
     * 初始化逻辑删除
     */
    public void initLogic(Object logicDeleteValue, Object logicNotDeleteValue) {
        if (logicDeleteValue instanceof CharSequence) {
            logicDeleteValue = String.format("'%s'", logicDeleteValue);
        }
        this.logicDeleteUpdateSql = DbUtil.formatLogicSql(alias, logicColumn, logicDeleteValue);

        if (logicNotDeleteValue instanceof CharSequence) {
            logicNotDeleteValue = String.format("'%s'", logicNotDeleteValue);
        }
        this.logicNotDeleteValue = logicNotDeleteValue;
        this.logicDeleteQuerySql = DbUtil.formatLogicSql(alias, logicColumn, logicNotDeleteValue);
    }

    /**
     * 多个主键的条件
     */
    public String createKeysCondition(Collection<? extends Serializable> keys) {
        Asserts.npe(keys);
        this.checkParams(keys);
        String condition = "";
        DbKeyParserModel<T> keyParserModel = getKeyParserModel();

        try {
            StringJoiner symbols = new StringJoiner(Constants.SEPARATOR_COMMA_2, Constants.BRACKETS_LEFT, Constants.BRACKETS_RIGHT);
            IntStream.range(0, keys.size()).mapToObj(i -> Constants.QUEST).forEach(symbols::add);

            if (this.checkLogicFieldIsExist()) {
                condition = String.format("AND %s IN %s", keyParserModel.getFieldSql(), symbols);
            } else {
                condition = String.format("%s IN %s", keyParserModel.getFieldSql(), symbols);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return condition;
    }


    /**
     * 一个主键的条件
     */
    public String createKeyCondition(Serializable key) {
        Asserts.npe(key);
        this.checkParams(Collections.singletonList(key));
        String condition = "";
        DbKeyParserModel<T> keyParserModel = getKeyParserModel();
        try {
            if (this.checkLogicFieldIsExist()) {
                condition = DbUtil.formatSqlAndCondition(keyParserModel.getFieldSql());
            } else {
                condition = DbUtil.formatSqlCondition(keyParserModel.getFieldSql());
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return condition;
    }

    /**
     * 检验参数的合法性
     */
    private void checkParams(Collection<? extends Serializable> keys) {
        if (JudgeUtil.isEmpty(keyParserModel)) {
            ExThrowsUtil.toCustom("%s 中未找到 @DbKey注解, 猜测该类或父类不存在主键字段，或没有标注@DbKey注解来表示主键", entityClass);
        }

        if (keys.stream().noneMatch(x -> CustomUtil.isKeyAllowType(keyParserModel.getType(), x))) {
            ExThrowsUtil.toCustom("不允许的主键参数: " + keys);
        }
    }

    /**
     * 添加逻辑删除的条件
     * <br/> 若存在逻辑删除的条件，则在条件前拼接逻辑删除的条件
     */
    public FullSqlConditionExecutor addLogicCondition(String condition) {
        return () -> {
            boolean isExist = checkLogicFieldIsExist();
            if (StrUtils.isBlank(condition)) {
                return isExist ? Constants.WHERE + getLogicDeleteQuerySql() : Constants.EMPTY;
            }
            if (isExist) {
                return Constants.WHERE + getLogicDeleteQuerySql() + condition.trim();
            }
            return Constants.WHERE + DbUtil.trimSqlCondition(condition);
        };
    }


}
