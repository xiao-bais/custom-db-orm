package com.custom.action.interfaces;

import com.custom.comm.enums.SqlExecTemplate;

/**
 * @author Xiao-Bai
 * @date 2022/10/27 12:41
 * 创建sql的接口
 */
public interface SqlBuilder {

    /**
     * sql构建，利用SQL模板
     * @param template sql模板枚举
     * @return
     */
    String create(SqlExecTemplate template);
}
