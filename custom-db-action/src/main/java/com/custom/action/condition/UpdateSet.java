package com.custom.action.condition;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/27 0027 11:19
 * @Desc update
 */
public interface UpdateSet<Param, Children> {

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
