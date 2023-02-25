package com.custom.jdbc.executebody;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 执行增删改操作的执行体
 * @author  Xiao-Bai
 * @since  2022/6/17 21:50
 */
public class SaveExecutorBody<T> extends BaseExecutorBody {

    /**
     * 要插入的数据
     */
    private List<T> dataList;

    /**
     * 主键的java字段
     */
    private Field keyField;

    public SaveExecutorBody(List<T> dataList, Field keyField, String prepareSql, boolean sqlPrintSupport, Object[] sqlParams) {
        super(prepareSql, sqlPrintSupport, sqlParams);
        this.dataList = dataList;
        this.keyField = keyField;
    }

    public SaveExecutorBody(String prepareSql, boolean sqlPrintSupport, Object[] sqlParams) {
        super(prepareSql, sqlPrintSupport, sqlParams);
    }

    public SaveExecutorBody(String prepareSql, Object[] sqlParams) {
        super(prepareSql, true, sqlParams);
    }

    public List<T> getDataList() {
        return dataList;
    }

    public Field getKeyField() {
        return keyField;
    }

}
