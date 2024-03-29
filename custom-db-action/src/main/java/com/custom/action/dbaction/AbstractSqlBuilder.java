package com.custom.action.dbaction;

import com.custom.action.autofill.CustomFillHandler;
import com.custom.action.autofill.CustomFillHelper;
import com.custom.action.autofill.CustomTableFill;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.action.core.DbFieldParserModel;
import com.custom.action.core.DbKeyParserModel;
import com.custom.action.core.TableInfoCache;
import com.custom.action.core.TableParseModel;
import com.custom.action.util.DbUtil;
import com.custom.comm.enums.FillStrategy;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.*;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.DatabaseAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.io.Serializable;
import java.util.*;
import java.util.stream.IntStream;

/**
 * sql操作模板父类
 * @author   Xiao-Bai
 * @since  2022/4/3 17:33
 */
@SuppressWarnings("unchecked")
public abstract class AbstractSqlBuilder<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSqlBuilder.class);

    private String table;
    private String alias;
    private Class<T> entityClass;
    private DbKeyParserModel<T> keyParserModel;
    private List<DbFieldParserModel<T>> fieldParserModels;
    private Map<String, String> columnMapper;
    private JdbcSqlSessionFactory sqlSessionFactory;
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
    /**
     * 表填充辅助对象
     */
    private CustomFillHelper<T> fillHelper;

    /**
     * 创建对应的sql
     */
    public abstract String createTargetSql();
    public abstract String createTargetSql(boolean primaryTable);
    public abstract String createTargetSql(Object obj, List<Object> sqlParams);

    public String getTable() {
        return table;
    }

    public String getAlias() {
        return alias;
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
        int order = sqlSessionFactory.getDbDataSource().getOrder();
        Boolean existsLogic = TableInfoCache.isExistsLogic(order, table);
        if (existsLogic != null) {
            return existsLogic;
        }
        DatabaseAdapter databaseAdapter = sqlSessionFactory.getDatabaseAdapter();
        boolean conBool = databaseAdapter.existColumn(table, logicColumn);
        TableInfoCache.setTableLogic(order, table, conBool);
        return conBool;
    }

    /**
     * 添加参数值
     */
    public void addParams(Object val, List<Object> sqlParams) {
        if (Objects.isNull(sqlParams)) {
            sqlParams = new ArrayList<>();
        }
        if (val instanceof List) {
            sqlParams.addAll((List<Object>) val);
            return;
        }
        sqlParams.add(val);
    }

    protected void injectTableInfo(TableParseModel<T> tableSqlBuilder, JdbcSqlSessionFactory sqlSessionFactory) {
        this.table = tableSqlBuilder.getTable();
        this.alias = tableSqlBuilder.getAlias();
        this.keyParserModel = tableSqlBuilder.getKeyParserModel();
        this.fieldParserModels = tableSqlBuilder.getDbFieldParseModels();
        this.columnMapper = tableSqlBuilder.getColumnMapper();
        this.entityClass = tableSqlBuilder.getEntityClass();
        this.sqlSessionFactory = sqlSessionFactory;

        // 设置逻辑删除字段
        DbCustomStrategy customStrategy = sqlSessionFactory.getDbCustomStrategy();
        this.logicColumn = customStrategy.getDbFieldDeleteLogic();
        this.logicSqlInitialize(customStrategy.getDeleteLogicValue(), customStrategy.getNotDeleteLogicValue());
        // 加载自动填充对象
        this.loadFillObjectInfo();
    }

    /**
     * 获取主键的值
     */
    public Object primaryKeyVal(T currEntity) {
        if (keyParserModel == null) {
            return null;
        }
        return keyParserModel.getValue(currEntity);
    }

    /**
     * 初始化逻辑删除
     */
    public void logicSqlInitialize(Object logicDeleteValue, Object logicNotDeleteValue) {
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
        AssertUtil.npe(keys);
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
        AssertUtil.npe(key);
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
            throw new CustomCheckException("%s 中未找到 @DbKey注解, 猜测该类或父类不存在主键字段，或没有标注@DbKey注解来表示主键", entityClass);
        }
        AssertUtil.notEmpty(keys, "primary key(s) cannot be empty.");
        if (keys.stream().noneMatch(x -> CustomUtil.isKeyAllowType(keyParserModel.getType(), x))) {
            throw new CustomCheckException("不允许的主键参数: " + keys);
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
                String finalCondition = "(" + DbUtil.trimSqlCondition(condition) + ")";
                return Constants.WHERE + getLogicDeleteQuerySql() + Constants.AND + " " + finalCondition;
            }
            return Constants.WHERE + DbUtil.trimSqlCondition(condition);
        };
    }


    /**
     * 查找填充值
     */
    protected Object findFillValue(String fieldName, Class<?> fieldType, FillStrategy strategy) {
        if (fillHelper == null) {
            return null;
        }
        return fillHelper.getFillValue(fieldName, fieldType, strategy);
    }

    /**
     * 是否存在自动填充
     */
    protected boolean existFill() {
        return this.fillHelper != null;
    }


    /**
     * 获取该类的
     */
    private void loadFillObjectInfo() {
        CustomFillHandler fillHandler;

        try {
            fillHandler = CustomApp.getBean(CustomFillHandler.class);
        } catch (NoSuchBeanDefinitionException e) {
            fillHandler = null;
        }
        if (fillHandler == null) {
            return;
        }
        List<CustomTableFill> tableFillList = this.handleMergeFills(fillHandler);
        if (tableFillList.isEmpty()) {
            return;
        }
        Optional<CustomTableFill> first = tableFillList.stream()
                .filter(e -> !e.isGlobalFill() && e.getTarget().isAssignableFrom(entityClass))
                .findFirst();
        CustomTableFill tableFill = first.orElseGet(() ->
                tableFillList.stream()
                        .filter(CustomTableFill::isGlobalFill)
                        .findFirst()
                        .orElse(null)
        );
        if (tableFill != null) {
            this.fillHelper = new CustomFillHelper<>(entityClass, tableFill);
        }
    }

    /**
     * 合并填充对象
     */
    private List<CustomTableFill> handleMergeFills(CustomFillHandler fillHandler) {
        List<CustomTableFill> tableFillList = new ArrayList<>();
        CustomTableFill fill = CustomTableFill.builder();
        fillHandler.handle(fill);
        fillHandler.handleMany(tableFillList);
        fill.globalFill();
        tableFillList.add(fill);
        if (tableFillList.stream().filter(CustomTableFill::isGlobalFill).count() > 1) {
            throw new CustomCheckException("错误配置：不允许出现两个或以上的全局填充对象配置");
        }
        return tableFillList;
    }


}
