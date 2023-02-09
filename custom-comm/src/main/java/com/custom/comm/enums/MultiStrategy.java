package com.custom.comm.enums;

/**
 * @author  Xiao-Bai
 * @since 2022/12/16 0016 17:01
 */
public enum MultiStrategy {


    /**
     * 默认不做处理，只查询第一层
     */
    NONE,

    /**
     * 递归查询(一对多与一对一两者通用)
     * <br/> 查询会组成一颗树，一旦遇到树上的任何一个已存在的节点
     * <br/> 则该节点不再继续往下递归，而是开始下一个节点的查询
     */
    RECURSION,

    /**
     * 抛出异常(一对多与一对一两者通用)
     * <br/> 同上，也是会进行递归查询，一旦遇到树上的任何一个已存在的节点，则抛出异常
     */
    ERROR




}
