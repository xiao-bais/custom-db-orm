package com.custom.dbaction;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.SymbolConst;
import com.custom.sqlparser.DbKeyParserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author Xiao-Bai
 * @date 2021/12/1 23:44
 * @desc:
 */
public abstract class AbstractTableModel<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTableModel.class);

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
        JudgeUtilsAx.checkObjNotNull(x);
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

    /**
     * 将值设置进实体的指定属性中
     */
    protected void setFieldValue(T entity, Field field, Object value) {
        JudgeUtilsAx.checkObjNotNull(entity);
        try {
            field.setAccessible(true);
            field.set(entity, value);
            field.setAccessible(false);
        }catch (IllegalAccessException e) {
            logger.error(e.toString(), e);
        }
    }


    protected abstract String buildTableSql();
    protected abstract Object getValue(T x);
    protected abstract void setValue(Object value);
    public abstract String getFieldSql();
    protected abstract String getSelectFieldSql();
    protected abstract String getSelectFieldSql(String column);

}
