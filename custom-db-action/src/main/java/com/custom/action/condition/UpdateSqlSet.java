package com.custom.action.condition;

/**
 * update set
 * @author   Xiao-Bai
 * @since  2022/8/2 22:36
 */
public interface UpdateSqlSet<Param, Children> {

    /**
     * update set
     * @param condition 条件成立，则加入set
     * @param column set的列
     * @param val set的值
     * @return Children
     */
    Children set(boolean condition, Param column, Object val);
    default Children set(Param column, Object val) {
        return set(true, column, val);
    }

    /**
     * update set
     * @param condition 条件成立，则加入set
     * @param setSql a.name = ?, a.age = ?
     * @param params 张三, 18
     * @return
     */
    Children setSql(boolean condition, String setSql, Object... params);
    default Children setSql(String setSql, Object... params) {
        return setSql(true, setSql, params);
    }










}
