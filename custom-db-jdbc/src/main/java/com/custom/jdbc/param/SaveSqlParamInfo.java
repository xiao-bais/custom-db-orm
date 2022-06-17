package com.custom.jdbc.param;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/17 21:50
 * @Desc
 */
public class SaveSqlParamInfo<T> extends BaseSqlParamInfo{

    /**
     * 要插入的数据
     */
    private List<T> dataList;

    /**
     * 主键的java字段
     */
    private Field keyField;

    public SaveSqlParamInfo(List<T> dataList, Field keyField, String prepareSql, boolean sqlPrintSupport, Object[] sqlParams) {
        super(prepareSql, sqlPrintSupport, sqlParams);
        this.dataList = dataList;
        this.keyField = keyField;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public Field getKeyField() {
        return keyField;
    }

    public void setKeyField(Field keyField) {
        this.keyField = keyField;
    }
}
