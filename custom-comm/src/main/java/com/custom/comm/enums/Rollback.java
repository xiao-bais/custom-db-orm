package com.custom.comm.enums;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/12 14:45
 * @Desc 事务回滚类型
 */
public enum Rollback {

    /**
     * 只在当前操作有效，属于单次提交的事务，也只会回滚当前提交的操作（若开启事务回滚后，则为默认回滚类型）
     */
    CURRENT,

    /**
     * 方法级别的事务，只对于单个方法类的事务做回滚
     */
    METHOD,

    /**
     * 可回滚整个接口的所有事务，属于请求类型的事务（若开启事务回滚后，则为全局事务）
     */
    REQUEST

}
