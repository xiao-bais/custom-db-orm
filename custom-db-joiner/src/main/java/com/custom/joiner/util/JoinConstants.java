package com.custom.joiner.util;

/**
 * @author Xiao-Bai
 * @date 2022/8/28 21:08
 * @desc
 */
public class JoinConstants {

    public final static String SPACE = " ";
    public final static String JOIN = "join";
    public final static String LEFT = "left";
    public final static String RIGHT = "right";
    public final static String INNER = "inner";

    /**
     * 左连接
     */
    public final static String LEFT_JOIN = LEFT + SPACE + JOIN + SPACE;

    /**
     * 右连接
     */
    public final static String RIGHT_JOIN = RIGHT + SPACE + JOIN + SPACE;

    /**
     * 内连接
     */
    public final static String INNER_JOIN = INNER + SPACE + JOIN + SPACE;

    /**
     * 表的别名
     */
    public final static String TABLE_ALIAS = SPACE + "a_" + CustomCharUtil.nextStr(8);









}
