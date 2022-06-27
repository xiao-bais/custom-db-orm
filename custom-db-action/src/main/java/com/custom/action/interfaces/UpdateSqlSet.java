package com.custom.action.interfaces;

import java.util.function.Supplier;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/27 0027 12:01
 * @Desc update set
 */
public interface UpdateSqlSet {

    /**
     * 返回一个可以set的sql
     */
    Supplier<String> set();
}
