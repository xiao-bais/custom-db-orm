package com.custom.action.dbaction;

import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.action.sqlparser.DbFieldParserModel;
import com.custom.action.sqlparser.DbKeyParserModel;
import com.custom.action.sqlparser.TableParseModel;
import com.custom.action.util.DbUtil;
import com.custom.action.condition.DefaultColumnParseHandler;
import com.custom.comm.Asserts;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtil;
import com.custom.comm.SymbolConstant;
import com.custom.configuration.DbCustomStrategy;
import com.custom.jdbc.CustomConfigHelper;
import com.custom.jdbc.CustomSelectJdbcBasicImpl;
import com.custom.jdbc.CustomUpdateJdbcBasicImpl;
import com.custom.jdbc.GlobalDataHandler;
import com.custom.jdbc.select.CustomSelectJdbcBasic;
import com.custom.jdbc.update.CustomUpdateJdbcBasic;
import com.custom.jdbc.condition.SaveSqlParamInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Xiao-Bai
 * @date 2022/4/3 17:33
 * @desc: sql操作模板父类
 */
@SuppressWarnings("unchecked")
public abstract class AbstractSqlBuilder<T> {

    private String table;
    private String alias;
    private T entity;
    private List<T> entityList;
    private Class<T> entityClass;
    private DbKeyParserModel<T> keyParserModel;
    private List<DbFieldParserModel<T>> fieldParserModels;
    private Map<String, String> fieldMapper;
    private Map<String, String> columnMapper;
    private CustomSelectJdbcBasic selectJdbc;
    private CustomUpdateJdbcBasic updateJdbc;
    private ColumnParseHandler<T> columnParseHandler;
    private Boolean primaryTable = false;
    private String logicColumn;
    private Object logicNotDeleteValue;
    private String logicDeleteQuerySql;
    private String logicDeleteUpdateSql;
    private List<Object> sqlParams = new ArrayList<>();

    // 构建sql语句
    public abstract String createTargetSql();

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
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
        this.columnParseHandler = new DefaultColumnParseHandler<>(entityClass);
    }


    public void setSqlParams(List<Object> sqlParams) {
        if (JudgeUtil.isNotEmpty(sqlParams)) {
            this.sqlParams = sqlParams;
        }
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

    public ColumnParseHandler<T> getColumnParseHandler() {
        return columnParseHandler;
    }

    public Boolean getPrimaryTable() {
        return primaryTable;
    }

    public void setPrimaryTable(Boolean primaryTable) {
        this.primaryTable = primaryTable;
    }

    /**
     * 直接执行，属于内部执行
     */
    public void executeUpdateNotPrintSql(String sql) throws Exception {
        Asserts.npe(sql);
        updateJdbc.executeUpdate(new SaveSqlParamInfo<>(sql, false, null));
    }

    /**
     * 由于部分表可能没有逻辑删除字段，所以在每一次执行时，都需检查该表有没有逻辑删除的字段，以保证sql正常执行
     */
    public boolean checkLogicFieldIsExist() throws Exception {
        if (CustomUtil.isBlank(logicColumn)) {
            return false;
        }
        return DbUtil.checkLogicFieldIsExist(table, logicColumn, selectJdbc);
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
     * 添加参数值
     */
    public void addParams(Object val) {
        if (Objects.isNull(sqlParams)) {
            sqlParams = new ArrayList<>();
        }
        if (val instanceof List) {
            this.sqlParams.addAll((List<Object>)val);
        }
        this.sqlParams.add(val);
    }

    /**
     * 注入基础表字段数据
     * @param tableSqlBuilder
     */
    protected void injectTableInfo(TableParseModel<T> tableSqlBuilder) {
        this.table = tableSqlBuilder.getTable();
        this.alias = tableSqlBuilder.getAlias();
        this.keyParserModel = tableSqlBuilder.getKeyParserModel();
        this.fieldParserModels = tableSqlBuilder.getFieldParserModels();
        this.columnMapper = tableSqlBuilder.getColumnMapper();
        this.fieldMapper = tableSqlBuilder.getFieldMapper();

        CustomConfigHelper configHelper = (CustomConfigHelper)
                GlobalDataHandler.readGlobalObject(SymbolConstant.DATA_CONFIG);
        Asserts.npe(configHelper, "未找到可用的数据源");
        DbCustomStrategy customStrategy = configHelper.getDbCustomStrategy();

        // 设置逻辑删除字段
        this.logicColumn = customStrategy.getDbFieldDeleteLogic();
        this.initLogic(customStrategy.getDeleteLogicValue(), customStrategy.getNotDeleteLogicValue());

        // 设置jdbc执行对象
        this.selectJdbc = new CustomSelectJdbcBasicImpl(
                configHelper.getDbDataSource(), customStrategy);
        this.updateJdbc = new CustomUpdateJdbcBasicImpl(
                configHelper.getDbDataSource(), customStrategy);
    }

    /**
     * 清空暂存
     */
    public void clear() {
        this.entityList = new ArrayList<>();
        setEntity(null);
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



}
