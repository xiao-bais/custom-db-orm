package com.custom.jdbc.condition;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/17 0:57
 * @Desc sql参数对象
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

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

}
