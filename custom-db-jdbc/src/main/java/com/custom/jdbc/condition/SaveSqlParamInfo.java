package com.custom.jdbc.condition;

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

    public SaveSqlParamInfo(String prepareSql, boolean sqlPrintSupport, Object[] sqlParams) {
        super(prepareSql, sqlPrintSupport, sqlParams);
    }

    public SaveSqlParamInfo(String prepareSql, boolean sqlPrintSupport) {
        super(prepareSql, sqlPrintSupport, new Object[]{});
    }

    public List<T> getDataList() {
        return dataList;
    }

    public Field getKeyField() {
        return keyField;
    }

}
