package com.custom.action.interfaces;

/**
 * @author Xiao-Bai
 * @date 2021/11/4 20:44
 * @desc: 添加逻辑删除字段的部分sql
 */
public interface FullSqlConditionExecutor {

    String execute() throws Exception;
}
