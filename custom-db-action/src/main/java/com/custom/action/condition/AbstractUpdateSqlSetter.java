package com.custom.action.condition;

import com.custom.action.util.DbUtil;
import com.custom.comm.Asserts;
import com.custom.comm.CustomUtil;
import com.custom.comm.SymbolConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/27 0027 12:01
 * @Desc 修改操作的sqlSet实体
 * Param - 参数的类型
 * T - 实体类型
 * Child - 子类类型
 */
public abstract class AbstractUpdateSqlSetter<T> {

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
     * update set
     * @param condition 条件成立，则加入set
     * @param column set的列
     * @param val set的值
     * @return Children
     */
    public AbstractUpdateSqlSetter<T> set(boolean condition, String column, Object val) {
        return addSetSql(condition, column, val);
    }
    public AbstractUpdateSqlSetter<T> set(String column, Object val) {
        return set(true, column, val);
    }
    public AbstractUpdateSqlSetter<T> set(boolean condition, SFunction<T, ?> column, Object val) {
        return addSetSql(condition, column, val);
    }
    public AbstractUpdateSqlSetter<T> set(SFunction<T, ?> column, Object val) {
        return set(true, column, val);
    }

    /**
     * update set
     * @param condition 条件成立，则加入set
     * @param setSql a.name = ?, a.age = ?
     * @param params 张三, 18
     * @return
     */
    public AbstractUpdateSqlSetter<T> setSql(boolean condition, String setSql, Object... params) {
        if (condition) {
            Asserts.notNull(setSql, "setSql cannot be null");
            this.sqlSetter.add(setSql);
            this.setParams.addAll(Arrays.asList(params));
        }
        return this;
    }
    public AbstractUpdateSqlSetter<T> setSql(String setSql, Object... params) {
        return setSql(true, setSql, params);
    }


    private AbstractUpdateSqlSetter<T> addSetSql(boolean condition, String column, Object val) {
        if (condition) {
            Asserts.notNull(column, "column cannot be null");
            this.sqlSetter.add(DbUtil.formatSetSql(column));
            Asserts.isIllegal(!CustomUtil.isBasicType(val),
                    String.format("Parameter types of type '%s' are not supported", val.getClass()));
            this.setParams.add(val);
        }
        return this;
    }

    private AbstractUpdateSqlSetter<T> addSetSql(boolean condition, SFunction<T, ?> column, Object val) {
        String originColumn = this.columnParseHandler.getColumn(column);
        return this.addSetSql(condition, originColumn, val);
    }


    public AbstractUpdateSqlSetter(Class<T> entityClass) {
        this.sqlSetter = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
        this.setParams = new ArrayList<>();
        this.entityClass = entityClass;
        this.columnParseHandler = new ColumnParseHandler<>(entityClass);
    }








}
