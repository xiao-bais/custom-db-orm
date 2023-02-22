package com.custom.jdbc.condition;

/**
 * 查询的执行体
 * @author  Xiao-Bai
 * @since  2022/6/17 0:57
 */
public class SelectExecutorBody<T> extends BaseExecutorBody {

    /**
     * 泛型实体Class对象
     */
    private Class<T> entityClass;


    public SelectExecutorBody(Class<T> entityClass, String selectSql, boolean sqlPrintSupport, Object[] sqlParams) {
        super(selectSql, sqlPrintSupport, sqlParams);
        this.entityClass = entityClass;
    }

    public SelectExecutorBody(Class<T> entityClass, String selectSql, boolean sqlPrintSupport) {
        super(selectSql, sqlPrintSupport, new Object[]{});
        this.entityClass = entityClass;
    }

    public SelectExecutorBody(Class<T> entityClass, String selectSql) {
        super(selectSql, true, new Object[]{});
        this.entityClass = entityClass;
    }

    public SelectExecutorBody(Class<T> entityClass, String selectSql, Object[] sqlParams) {
        super(selectSql, true, sqlParams);
        this.entityClass = entityClass;
    }

    public SelectExecutorBody(String selectSql, Object[] sqlParams) {
        super(selectSql, true, sqlParams);
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

}
