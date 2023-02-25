package com.custom.jdbc.executebody;

/**
 * 查询的执行体
 * @author  Xiao-Bai
 * @since  2022/6/17 0:57
 */
public class SelectExecutorBody<T> extends BaseExecutorBody {

    /**
     * 泛型实体Class对象
     */
    private Class<T> mappedType;


    public SelectExecutorBody(Class<T> mappedType, String selectSql, boolean sqlPrintSupport, Object[] sqlParams) {
        super(selectSql, sqlPrintSupport, sqlParams);
        this.mappedType = mappedType;
    }

    public SelectExecutorBody(Class<T> mappedType, String selectSql, boolean sqlPrintSupport) {
        super(selectSql, sqlPrintSupport, new Object[]{});
        this.mappedType = mappedType;
    }

    public SelectExecutorBody(Class<T> mappedType, String selectSql) {
        super(selectSql, true, new Object[]{});
        this.mappedType = mappedType;
    }

    public SelectExecutorBody(Class<T> mappedType, String selectSql, Object[] sqlParams) {
        super(selectSql, true, sqlParams);
        this.mappedType = mappedType;
    }

    public SelectExecutorBody(String selectSql, Object[] sqlParams) {
        super(selectSql, true, sqlParams);
    }

    public Class<T> getMappedType() {
        return mappedType;
    }

}
