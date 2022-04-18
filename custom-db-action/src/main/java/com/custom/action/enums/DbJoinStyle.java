package com.custom.action.enums;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/10 18:01
 * @Desc：
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
