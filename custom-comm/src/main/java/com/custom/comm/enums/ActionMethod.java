package com.custom.comm.enums;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/19 0019 16:50
 * @Desc 执行方法
 */
public enum ActionMethod {

    /**
     * 插入
     */
    INSERT("insert", 1, "插入一条记录"),
    INSERT_BATCH("insertBatch", 1, "插入一条记录"),

    /**
     * 删除
     */
    DELETE_BY_KEY("deleteByKey", 2, "根据主键删除一条记录"),
    DELETE_BATCH_KEYS("deleteBatchKeys", 2, "根据主键删除多条记录"),
    DELETE_CONDITION("deleteCondition", 3, "根据条件删除n条记录"),
    DELETE_WRAPPER("deleteWrapper", 1, "根据条件删除n条记录"),

    /**
     * 修改
     */
    UPDATE_BY_KEY("updateByKey", 1, "根据主键修改一条记录"),
    UPDATE_COLUMN_BY_KEY("updateColumnByKey", 2, "根据主键修改指定字段"),
    UPDATE_BY_CONDITION("updateByCondition", 3, "根据条件修改"),
    UPDATE_BY_WRAPPER("updateByWrapper", 1, "根据条件修改"),


    /**
     * 查询
     */
    SELECT_LIST("selectList", 3, "根据条件查询多条记录"),
    SELECT_PAGE_ROWS("selectPageRows", 4, "根据条件查询多条记录并分页"),
    SELECT_ONE_BY_KEY("selectOneByKey", 3, "根据主键查询一条记录"),
    SELECT_BATCH_BY_KEYS("selectBatchByKeys", 3, "根据主键查询多条记录"),
    SELECT_ONE_BY_CONDITION("selectOneByCondition", 3, "根据条件查询一条记录"),

    SELECT_LIST_WRAPPER("selectListWrapper", 1, "根据条件查询多条记录"),
    SELECT_PAGE_ROWS_WRAPPER("selectPageRowsWrapper", 1, "根据条件查询多条记录并分页"),
    SELECT_ONE_WRAPPER("selectOneWrapper", 1, "根据条件查询一条记录"),
    SELECT_COUNT("selectCount", 1, "根据条件查询记录行数"),

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
     * 方法说明
     */
    private final String description;

    ActionMethod(String methodName, int paramNumber, String description) {
        this.methodName = methodName;
        this.paramNumber = paramNumber;
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
}
