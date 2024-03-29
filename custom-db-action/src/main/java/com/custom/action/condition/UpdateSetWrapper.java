package com.custom.action.condition;

import com.custom.action.core.TableSimpleSupport;
import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.comm.utils.AssertUtil;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * update set 包装类
 * @author   Xiao-Bai
 * @since  2022/8/6 17:45
 */
public abstract class UpdateSetWrapper<T> {

    /**
     * sql片段
     */
    private final StringJoiner sqlSetter;
    /**
     * set的参数值
     */
    private final List<Object> setParams;
    /**
     * 实体Class对象
     */
    private final Class<T> entityClass;
    /**
     * SFunction接口实体字段解析对象
     */
    private final ColumnParseHandler<T> columnParseHandler;

    public List<Object> getSetParams() {
        return setParams;
    }

    public UpdateSetWrapper(Class<T> entityClass) {
        this.sqlSetter = new StringJoiner(Constants.SEPARATOR_COMMA_2);
        this.setParams = new ArrayList<>();
        this.entityClass = entityClass;
        TableSimpleSupport<T> simpleSupport = new TableSimpleSupport<>(entityClass);
        this.columnParseHandler = new DefaultColumnParseHandler<>(entityClass, simpleSupport);
    }

    public Class<T> thisEntityClass() {
        return entityClass;
    }

    protected ColumnParseHandler<T> getColumnParseHandler() {
        return columnParseHandler;
    }

    protected void addSqlSetter(StringJoiner sqlSetter) {
        this.sqlSetter.merge(sqlSetter);
    }

    protected void addSqlSetter(String sqlSetter) {
        this.sqlSetter.add(sqlSetter);
    }

    protected void addParams(Object val) {
        AssertUtil.notNull(val, "params cannot be empty");
        CustomUtil.addParams(this.setParams, val);
    }

    public StringJoiner getSqlSetter() {
        return sqlSetter;
    }
}
