package com.custom.jdbc.executebody;

/**
 * 查询Map类型的执行体
 * @author  Xiao-Bai
 * @since  2022/10/25 23:08
 */
public class SelectMapExecutorBody<K, V> extends SelectExecutorBody<V> {

    private final Class<K> keyType;

    public SelectMapExecutorBody(String prepareSql, boolean sqlPrintSupport, Object[] sqlParams, Class<K> keyType, Class<V> valueType) {
        super(valueType, prepareSql, sqlPrintSupport, sqlParams);
        this.keyType = keyType;
    }

    public Class<K> getKeyType() {
        return keyType;
    }

    public Class<V> getValueType() {
        return getMappedType();
    }
}
