package com.custom.action.condition;

import com.custom.comm.Asserts;
import com.custom.comm.SymbolConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @Author Xiao-Bai
 * @Date 2022/8/6 17:45
 * @Desc
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
    /**
     * 本次修改是否存在条件
     */
    private boolean existCondition = true;

    public List<Object> getSetParams() {
        return setParams;
    }

    public boolean isExistCondition() {
        return existCondition;
    }

    public void existCondition() {
        this.existCondition = true;
    }
    public void notExistCondition() {
        this.existCondition = false;
    }

    public UpdateSetWrapper(Class<T> entityClass) {
        this.sqlSetter = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
        this.setParams = new ArrayList<>();
        this.entityClass = entityClass;
        this.columnParseHandler = new ColumnParseHandler<>(entityClass);
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

    protected void addParams(List<Object> params) {
        Asserts.notNull(params, "params cannot be empty");
        this.setParams.addAll(params);
    }

    public StringJoiner getSqlSetter() {
        return sqlSetter;
    }
}
