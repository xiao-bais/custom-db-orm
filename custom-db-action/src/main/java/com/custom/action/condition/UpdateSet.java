package com.custom.action.condition;

/**
 * @Author Xiao-Bai
 * @Date 2022/8/2 22:36
 * @Desc
 */
public interface UpdateSet<Param, T, Children> {

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










}
