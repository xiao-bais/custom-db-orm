package com.custom.comm.enums;

import com.custom.comm.utils.Constants;

/**
 * sql排序枚举
 * @author  Xiao-Bai
 * @since 2022/3/5 22:53
 */
public enum SqlOrderBy {

    /**
     * 默认，升序（从小到大）
     */
    ASC(Constants.ASC),


    /**
     * 降序（从大到小）
     */
    DESC(Constants.DESC);


    private final String name;

    SqlOrderBy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
