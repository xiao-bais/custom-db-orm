package com.custom.jdbc.param;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/17 0:57
 * @Desc sql参数对象
 */
public class SelectSqlParamInfo<T> {

    /**
     * 泛型实体Class对象
     */
    private Class<T> entityClass;

    /**
     * 查询的sql
     */
    private String selectSql;

    /**
     * 是否支持sql打印
     */
    private boolean sqlPrintSupport = true;

    /**
     * 是否支持查询多条记录
     */
    private boolean supportMoreResult;

    /**
     * sql参数
     */
    private Object[] sqlParams;

    public SelectSqlParamInfo(Class<T> entityClass, String selectSql, boolean sqlPrintSupport, Object[] sqlParams) {
        this.entityClass = entityClass;
        this.selectSql = selectSql;
        this.sqlPrintSupport = sqlPrintSupport;
        this.sqlParams = sqlParams;
    }

    public SelectSqlParamInfo(Class<T> entityClass, String selectSql, Object[] sqlParams) {
        this.entityClass = entityClass;
        this.selectSql = selectSql;
        this.sqlParams = sqlParams;
    }

    public SelectSqlParamInfo(String selectSql, boolean supportMoreResult, Object[] sqlParams) {
        this.selectSql = selectSql;
        this.supportMoreResult = supportMoreResult;
        this.sqlParams = sqlParams;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public String getSelectSql() {
        return selectSql;
    }

    public void setSelectSql(String selectSql) {
        this.selectSql = selectSql;
    }

    public boolean isSqlPrintSupport() {
        return sqlPrintSupport;
    }

    public void setSqlPrintSupport(boolean sqlPrintSupport) {
        this.sqlPrintSupport = sqlPrintSupport;
    }

    public Object[] getSqlParams() {
        return sqlParams;
    }

    public void setSqlParams(Object[] sqlParams) {
        this.sqlParams = sqlParams;
    }

    public boolean isSupportMoreResult() {
        return supportMoreResult;
    }

    public void setSupportMoreResult(boolean supportMoreResult) {
        this.supportMoreResult = supportMoreResult;
    }
}
