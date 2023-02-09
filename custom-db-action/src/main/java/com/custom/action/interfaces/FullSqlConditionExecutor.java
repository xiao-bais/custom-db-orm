package com.custom.action.interfaces;

/**
 * 添加逻辑删除字段的部分sql
 * @author  Xiao-Bai
 * @since  2021/11/4 20:44
 */
public interface FullSqlConditionExecutor {

    String execute() throws Exception;
}
