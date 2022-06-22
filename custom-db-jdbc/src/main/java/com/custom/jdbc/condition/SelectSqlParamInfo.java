package com.custom.jdbc.condition;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/17 0:57
 * @Desc sql参数对象
 */
public class SelectSqlParamInfo<T> extends BaseSqlParamInfo {

    /**
     * 泛型实体Class对象
     */
    private Class<T> entityClass;

    /**
     * 是否支持查询多条记录
     */
    private boolean supportMoreResult;

    private boolean isField;

    public boolean isField() {
        return isField;
    }

    public void setField(boolean field) {
        isField = field;
    }

    public SelectSqlParamInfo(Class<T> entityClass, String selectSql, boolean sqlPrintSupport, Object[] sqlParams) {
        super(selectSql, sqlPrintSupport, sqlParams);
        this.entityClass = entityClass;
    }

    public SelectSqlParamInfo(Class<T> entityClass, String selectSql, boolean sqlPrintSupport) {
        super(selectSql, sqlPrintSupport, new Object[]{});
        this.entityClass = entityClass;
    }

    public SelectSqlParamInfo(Class<T> entityClass, String selectSql) {
        super(selectSql, true, new Object[]{});
        this.entityClass = entityClass;
    }

    public SelectSqlParamInfo(Class<T> entityClass, String selectSql, Object[] sqlParams) {
        super(selectSql, true, sqlParams);
        this.entityClass = entityClass;
    }

    public SelectSqlParamInfo(String selectSql, boolean supportMoreResult, Object[] sqlParams) {
        super(selectSql, true, sqlParams);
        this.supportMoreResult = supportMoreResult;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public boolean isSupportMoreResult() {
        return supportMoreResult;
    }
}
