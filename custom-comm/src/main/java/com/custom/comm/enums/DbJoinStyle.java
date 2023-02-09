package com.custom.comm.enums;

/**
 * 表连接方式
 * @author  Xiao-Bai
 * @since 2022/3/10 18:01
 **/
public enum DbJoinStyle {

    /**
     * 内连接
     */
    INNER("inner join"),

    /**
     * 左连接
     */
    LEFT("left join"),

    /**
     * 右连接
     */
    RIGHT("right join");

    DbJoinStyle(String style) {
        this.style = style;
    }

    private final String style;

    public String getStyle() {
        return style;
    }
}
