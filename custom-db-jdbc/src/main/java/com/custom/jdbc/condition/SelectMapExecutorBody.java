package com.custom.jdbc.condition;

/**
 * 查询Map类型的执行体
 * @author  Xiao-Bai
 * @since  2022/10/25 23:08
 */
public class SelectMapExecutorBody<K, V> extends BaseExecutorBody {

    private final Class<K> keyType;

    private final Class<V> valueType;


    public SelectMapExecutorBody(String prepareSql, boolean sqlPrintSupport, Object[] sqlParams, Class<K> keyType, Class<V> valueType) {
        super(prepareSql, sqlPrintSupport, sqlParams);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public SelectMapExecutorBody(String prepareSql, boolean sqlPrintSupport, Class<K> keyType, Class<V> valueType) {
        super(prepareSql, sqlPrintSupport, new Object[]{});
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public Class<K> getKeyType() {
        return keyType;
    }

    public Class<V> getValueType() {
        return valueType;
    }
}
