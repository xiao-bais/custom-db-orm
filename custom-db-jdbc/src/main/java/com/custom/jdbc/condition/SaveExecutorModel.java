package com.custom.jdbc.condition;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/17 21:50
 * @Desc
 */
public class SaveExecutorModel<T> extends BaseExecutorModel<T> {

    /**
     * 要插入的数据
     */
    private List<T> dataList;

    /**
     * 主键的java字段
     */
    private Field keyField;

    public SaveExecutorModel(List<T> dataList, Field keyField, String prepareSql, boolean sqlPrintSupport, Object[] sqlParams) {
        super(prepareSql, sqlPrintSupport, sqlParams);
        this.dataList = dataList;
        this.keyField = keyField;
    }

    public SaveExecutorModel(String prepareSql, boolean sqlPrintSupport, Object[] sqlParams) {
        super(prepareSql, sqlPrintSupport, sqlParams);
    }

    public SaveExecutorModel(String prepareSql, Object[] sqlParams) {
        super(prepareSql, true, sqlParams);
    }

    public List<T> getDataList() {
        return dataList;
    }

    public Field getKeyField() {
        return keyField;
    }

}
