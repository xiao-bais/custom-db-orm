package com.custom.comm.enums;

import java.lang.reflect.Method;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/19 0019 16:50
 * @Desc 执行方法
 */
public enum ActionMethod {

    /**
     * 插入
     */
    INSERT("insert", 1, ExecuteMethod.INSERT, "插入一条记录"),
    INSERT_BATCH("insertBatch", 1, ExecuteMethod.INSERT, "插入一条记录"),

    /**
     * 删除
     */
    DELETE_BY_KEY("deleteByKey", 2, ExecuteMethod.DELETE, "根据主键删除一条记录"),
    DELETE_BATCH_KEYS("deleteBatchKeys", 2, ExecuteMethod.DELETE, "根据主键删除多条记录"),
    DELETE_CONDITION("deleteByCondition", 3, ExecuteMethod.DELETE, "根据条件删除n条记录"),
    DELETE_WRAPPER("deleteByCondition", 1, ExecuteMethod.DELETE, "根据条件删除n条记录"),

    /**
     * 修改
     */
    UPDATE_BY_KEY("updateByKey", 1, ExecuteMethod.UPDATE, "根据主键修改一条记录"),
    UPDATE_COLUMN_BY_KEY("updateColumnByKey", 2, ExecuteMethod.UPDATE, "根据主键修改指定字段"),
    UPDATE_BY_CONDITION("updateByCondition", 3, ExecuteMethod.UPDATE, "根据条件修改"),
    UPDATE_BY_WRAPPER("updateByCondition", 1, ExecuteMethod.UPDATE, "根据条件修改"),


    /**
     * 查询-普通
     */
    SELECT_LIST("selectList", 3, ExecuteMethod.SELECT, "根据条件查询多条记录"),
    SELECT_LIST_BY_SQL("selectListBySql", 3, ExecuteMethod.SELECT, "纯sql查询多条记录"),
    SELECT_PAGE_ROWS("selectPageRows", 4, ExecuteMethod.SELECT, "根据条件查询多条记录并分页"),
    SELECT_ONE_BY_KEY("selectOneByKey", 3, ExecuteMethod.SELECT, "根据主键查询一条记录"),
    SELECT_BATCH_BY_KEYS("selectBatchByKeys", 3, ExecuteMethod.SELECT, "根据主键查询多条记录"),
    SELECT_ONE_BY_CONDITION("selectOne", 3, ExecuteMethod.SELECT, "根据条件查询一条记录"),
    SELECT_ARRAYS("selectArrays", 3, ExecuteMethod.SELECT, "纯sql查询多个值"),

    /**
     * 查询-条件构造器
     */
    SELECT_LIST_WRAPPER("selectList", 1, ExecuteMethod.SELECT, "根据条件查询多条记录"),
    SELECT_PAGE_ROWS_WRAPPER("selectPageRows", 1, ExecuteMethod.SELECT, "根据条件查询多条记录并分页"),
    SELECT_ONE_WRAPPER("selectOne", 1, ExecuteMethod.SELECT, "根据条件查询一条记录"),
    SELECT_COUNT("selectCount", 1, ExecuteMethod.SELECT, "根据条件查询记录行数"),
    SELECT_OBJ("selectObj", 1, ExecuteMethod.SELECT, "根据条件查询一个值"),
    SELECT_OBJS("selectObjs", 1, ExecuteMethod.SELECT, "根据条件查询多个值"),
    SELECT_MAP("selectMap", 1, ExecuteMethod.SELECT, "根据条件查询一个对象映射到map"),
    SELECT_MAPS("selectMaps", 1, ExecuteMethod.SELECT, "根据条件查询多个对象映射到多个map组成集合"),
    SELECT_PAGE_MAPS("selectPageMaps", 1, ExecuteMethod.SELECT, "根据条件查询多个对象映射到多个map组成集合的分页数据"),


    ;

    /**
     * 方法名
     */
    private final String methodName;

    /**
     * 参数数量
     */
    private final int paramNumber;

    /**
     * 执行类型
     */
    private final ExecuteMethod executeMethod;

    /**
     * 方法说明
     */
    private final String description;

    ActionMethod(String methodName, int paramNumber, ExecuteMethod executeMethod, String description) {
        this.methodName = methodName;
        this.paramNumber = paramNumber;
        this.executeMethod = executeMethod;
        this.description = description;
    }


    public String getMethodName() {
        return methodName;
    }

    public int getParamNumber() {
        return paramNumber;
    }

    public String getDescription() {
        return description;
    }

    public ExecuteMethod getExecuteMethod() {
        return executeMethod;
    }
}
