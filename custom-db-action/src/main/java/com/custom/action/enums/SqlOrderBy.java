package com.custom.action.enums;

import com.custom.comm.SymbolConst;

/**
 * @author Xiao-Bai
 * @date 2022/3/5 22:53
 * @desc:sql排序枚举
 */
public enum SqlOrderBy {

    /**
     * 默认，升序（从小到大）
     */
    ASC(SymbolConst.ASC),


    /**
     * 降序（从大到小）
     */
    DESC(SymbolConst.DESC);


    private final String name;

    SqlOrderBy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
