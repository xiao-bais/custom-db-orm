package com.custom.dbaction;

import com.custom.dbconfig.SymbolConst;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Xiao-Bai
 * @date 2021/12/1 23:44
 * @desc:
 */
public abstract class AbstractTableModel<T> {

    private String table;

    private String alias;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * 获取实体中指定字段的值
     * x: 实例对象
     * fieldName: 字段名称
     */
    protected Object getFieldValue(T x, String fieldName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if(x == null) throw  new NullPointerException();
        Object value;
        String firstLetter;
        String getter;
        try {
            firstLetter = fieldName.substring(0, 1).toUpperCase();
            getter = SymbolConst.GET + firstLetter + fieldName.substring(1);
            Method method = x.getClass().getMethod(getter);
            value = method.invoke(x);
        }catch (NoSuchMethodException e){
            try {
                firstLetter = fieldName.substring(0, 1).toUpperCase();
                Method method = x.getClass().getMethod(SymbolConst.IS + firstLetter + fieldName.substring(1));
                value = method.invoke(x);
            }catch (NoSuchMethodException v) {
                Method method = x.getClass().getMethod(fieldName);
                value = method.invoke(x);
            }
        }
        return value;
    }

    public abstract String buildTableSql();
    public abstract Object getValue(T x);
    public abstract String getFieldSql();
    public abstract String getSelectFieldSql();
    public abstract String getSelectFieldSql(String column);

}
