package com.custom.action.interfaces;

import com.custom.comm.enums.SqlExecTemplate;

/**
 * sql构建的接口
 * @author  Xiao-Bai
 * @since  2022/10/27 12:41
 */
public interface SqlBuilder {

    /**
     * sql构建，利用SQL模板
     * @param template sql模板枚举
     * @return
     */
    String create(SqlExecTemplate template);
}
