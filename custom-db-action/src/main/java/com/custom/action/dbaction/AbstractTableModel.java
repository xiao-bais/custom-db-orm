package com.custom.action.dbaction;

import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.utils.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Xiao-Bai
 * @date 2021/12/1 23:44
 * @desc:
 */
public abstract class AbstractTableModel<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTableModel.class);

    /**
     * 表名
     */
    private String table;

    /**
     * 表的别名
     */
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
    protected Object getFieldValue(T x, String fieldName)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        return ReflectUtil.readFieldValue(x, fieldName);
    }

    /**
     * 将值设置进实体的指定属性中
     */
    protected void setFieldValue(T entity, Field field, Object value) {
        Asserts.npe(entity);
        try {
            ReflectUtil.writeFieldValue(value, entity, field.getName(), field.getType());
        }catch (NoSuchFieldException e) {
            logger.error(e.toString(), e);
        }
    }


    public abstract String createTableSql();
    protected abstract Object getValue(T x);
    protected abstract void setValue(Object value);
    public abstract String getFieldSql();
    protected abstract String getSelectFieldSql();

}
