package com.custom.action.join;

import com.custom.comm.enums.DbJoinStyle;

/**
 * @Author Xiao-Bai
 * @Date 2022/8/19 2:15
 * @Desc
 */
public class JoinRegister<WrapperChildren, Primary, Join> {

    /**
     * 主关系关联表
     */
    private Class<Primary> primaryTable;
    /**
     * 关联表
     */
    private Class<Join> joinTable;
    /**
     * 主关系关联表是否是主表
     */
    private boolean isJoinPrimary = false;

    private DbJoinStyle joinStyle = DbJoinStyle.LEFT;

    /**
     * 关联构造器
     */
    private JoinWrapper<WrapperChildren, Primary, Join> wrapper;


    public Class<Primary> getPrimaryTable() {
        return primaryTable;
    }

    public Class<Join> getJoinTable() {
        return joinTable;
    }

    public boolean isJoinPrimary() {
        return isJoinPrimary;
    }

    /**
     * 左连接
     */
    public JoinWrapper<WrapperChildren, Primary, Join> leftJoin(Class<Primary> primary, Class<Join> joinTale) {
        this.primaryTable = primary;
        this.joinTable = joinTale;
        return wrapper;
    }
    public JoinWrapper<WrapperChildren, Primary, Join> leftJoin(Class<Join> joinTale) {
        this.joinTable = joinTale;
        this.isJoinPrimary = true;
        return wrapper;
    }

    /**
     * 内连接
     */
    public JoinWrapper<WrapperChildren, Primary, Join> innerJoin(Class<Primary> primary, Class<Join> joinTale) {
        this.primaryTable = primary;
        this.joinTable = joinTale;
        return wrapper;
    }
    public JoinWrapper<WrapperChildren, Primary, Join> innerJoin(Class<Join> joinTale) {
        this.joinTable = joinTale;
        this.isJoinPrimary = true;
        return wrapper;
    }

    /**
     * 右连接
     */
    public JoinWrapper<WrapperChildren, Primary, Join> rightJoin(Class<Primary> primary, Class<Join> joinTale) {
        this.primaryTable = primary;
        this.joinTable = joinTale;
        return wrapper;
    }
    public JoinWrapper<WrapperChildren, Primary, Join> rightJoin(Class<Join> joinTale) {
        this.joinTable = joinTale;
        this.isJoinPrimary = true;
        return wrapper;
    }

    public JoinRegister() {
    }
}
