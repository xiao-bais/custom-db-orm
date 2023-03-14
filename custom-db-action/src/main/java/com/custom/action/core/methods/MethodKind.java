package com.custom.action.core.methods;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.condition.ConditionWrapper;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.interfaces.TransactionExecutor;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Xiao-Bai
 * @since 2023/3/8 20:24
 */
public enum MethodKind {

    SELECT_BY_KEY("selectByKey", 2, new Class<?>[]{Class.class, Serializable.class}),
    SELECT_BATCH_KEYS("selectBatchKeys", 2, new Class<?>[]{Class.class, Collection.class}),
    SELECT_LIST("selectList", 3, new Class<?>[]{Class.class, String.class, Object[].class}),
    SELECT_ONE("selectOne", 3, new Class<?>[]{Class.class, String.class, Object[].class}),
    SELECT_PAGE("selectPage", 4, new Class<?>[]{Class.class, String.class, DbPageRows.class, Object[].class}),
    SELECT_LIST_BY_SQL("selectListBySql", 3, new Class<?>[]{Class.class, String.class, Object[].class}),
    SELECT_ONE_BY_SQL("selectOneBySql", 3, new Class<?>[]{Class.class, String.class, Object[].class}),
    SELECT_OBJ_BY_SQL("selectObjBySql", 2, new Class<?>[]{String.class, Object[].class}),
    SELECT_OBJS_BY_SQL("selectObjsBySql", 2, new Class<?>[]{String.class, Object[].class}),
    SELECT_MAP("selectMap", 4, new Class<?>[]{Class.class, Class.class, String.class, Object[].class}),
    SELECT_LIST_BY_WRAPPER("selectList", 1, new Class<?>[]{ConditionWrapper.class}),
    SELECT_ONE_BY_WRAPPER("selectOne", 1, new Class<?>[]{ConditionWrapper.class}),
    SELECT_PAGE_BY_WRAPPER("selectPage", 1, new Class<?>[]{ConditionWrapper.class}),
    SELECT_COUNT_BY_WRAPPER("selectCount", 1, new Class<?>[]{ConditionWrapper.class}),
    SELECT_OBJS_BY_WRAPPER("selectObjs", 1, new Class<?>[]{ConditionWrapper.class}),
    SELECT_OBJ_BY_WRAPPER("selectObj", 1, new Class<?>[]{ConditionWrapper.class}),
    SELECT_ONE_MAP_BY_WRAPPER("selectOneMap", 1, new Class<?>[]{ConditionWrapper.class}),
    SELECT_LIST_MAP_BY_WRAPPER("selectListMap", 1, new Class<?>[]{ConditionWrapper.class}),
    SELECT_PAGE_MAP_BY_WRAPPER("selectPageMap", 1, new Class<?>[]{ConditionWrapper.class}),
    SELECT_MAP_BY_WRAPPER("selectMap", 3, new Class<?>[]{ConditionWrapper.class, Class.class, Class.class}),
    SELECT_ARRAYS("selectArrays", 3, new Class<?>[]{Class.class, String.class, Object[].class}),
    SELECT_ONE_BY_ENTITY("selectOne", 1, new Class<?>[]{Object.class}),
    SELECT_LIST_BY_ENTITY("selectList", 1, new Class<?>[]{Object.class}),
    SELECT_PAGE_BY_ENTITY("selectPage", 2, new Class<?>[]{Object.class, DbPageRows.class}),


    DELETE_BY_KEY("deleteByKey", 2, new Class<?>[]{Class.class, Serializable.class}),
    DELETE_BATCH_KEYS("deleteBatchKeys", 2, new Class<?>[]{Class.class, Serializable.class}),
    DELETE_BY_CONDITION("deleteByCondition", 3, new Class<?>[]{Class.class, String.class, Object[].class}),
    DELETE_SELECTIVE("deleteSelective", 1, new Class<?>[]{ConditionWrapper.class}),

    INSERT("insert", 1, new Class<?>[]{Object.class}),
    INSERT_BATCH("insertBatch", 1, new Class<?>[]{Collection.class}),

    UPDATE_BY_KEY("updateByKey", 1, new Class<?>[]{Object.class}),
    UPDATE_SELECTIVE_BY_WRAPPER("updateSelective", 2, new Class<?>[]{Object.class, ConditionWrapper.class}),
    UPDATE_BY_CONDITION("updateByCondition", 3, new Class<?>[]{Object.class, String.class, Object[].class}),
    UPDATE_SELECTIVE_BY_SQL_SET("updateSelective", 1, new Class<?>[]{AbstractUpdateSet.class}),

    SAVE("save", 1, new Class[]{Object.class}),
    EXECUTE_SQL("executeSql", 2, new Class<?>[]{String.class, Object[].class}),
    DROP_TABLES("dropTables", 1, new Class<?>[]{Class[].class}),
    CREATE_TABLES("createTables", 1, new Class<?>[]{Class[].class}),
    EXEC_TRANS("execTrans", 1, new Class<?>[]{TransactionExecutor.class}),
    CREATE_CHAIN("createChain", 1, new Class<?>[]{Class.class}),

    ;




    /**
     * 方法名称
     */
    private final String method;
    /**
     * 参数数量
     */
    private final int paramsNum;
    /**
     * 参数类型数组
     */
    private final Class<?>[] typeArr;

    MethodKind(String method, int paramsNum, Class<?>[] typeArr) {
        this.method = method;
        this.paramsNum = paramsNum;
        this.typeArr = typeArr;
    }

    public String getMethod() {
        return method;
    }

    public int getParamsNum() {
        return paramsNum;
    }

    public Class<?>[] getTypeArr() {
        return typeArr;
    }
}
