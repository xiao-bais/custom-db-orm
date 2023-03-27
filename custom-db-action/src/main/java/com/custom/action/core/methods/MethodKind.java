package com.custom.action.core.methods;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.syncquery.SyncQueryWrapper;
import com.custom.comm.enums.ExecuteMethod;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.interfaces.TransactionExecutor;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Xiao-Bai
 * @since 2023/3/8 20:24
 */
public enum MethodKind {

    SELECT_BY_KEY("selectByKey", ExecuteMethod.SELECT, new Class<?>[]{Class.class, Serializable.class}),
    SELECT_BATCH_KEYS("selectBatchKeys", ExecuteMethod.SELECT, new Class<?>[]{Class.class, Collection.class}),
    SELECT_LIST("selectList", ExecuteMethod.SELECT, new Class<?>[]{Class.class, String.class, Object[].class}),
    SELECT_ONE("selectOne", ExecuteMethod.SELECT, new Class<?>[]{Class.class, String.class, Object[].class}),
    SELECT_PAGE("selectPage", ExecuteMethod.SELECT, new Class<?>[]{Class.class, String.class, DbPageRows.class, Object[].class}),
    SELECT_LIST_BY_SQL("selectListBySql", ExecuteMethod.SELECT, new Class<?>[]{Class.class, String.class, Object[].class}),
    SELECT_ONE_BY_SQL("selectOneBySql", ExecuteMethod.SELECT, new Class<?>[]{Class.class, String.class, Object[].class}),
    SELECT_OBJ_BY_SQL("selectObjBySql", ExecuteMethod.SELECT, new Class<?>[]{String.class, Object[].class}),
    SELECT_OBJS_BY_SQL("selectObjsBySql", ExecuteMethod.SELECT, new Class<?>[]{String.class, Object[].class}),
    SELECT_MAP("selectMap", ExecuteMethod.SELECT, new Class<?>[]{Class.class, Class.class, String.class, Object[].class}),
    SELECT_LIST_BY_WRAPPER("selectList", ExecuteMethod.SELECT, new Class<?>[]{ConditionWrapper.class}),
    SELECT_ONE_BY_WRAPPER("selectOne", ExecuteMethod.SELECT, new Class<?>[]{ConditionWrapper.class}),
    SELECT_PAGE_BY_WRAPPER("selectPage", ExecuteMethod.SELECT, new Class<?>[]{ConditionWrapper.class}),
    SELECT_COUNT_BY_WRAPPER("selectCount", ExecuteMethod.SELECT, new Class<?>[]{ConditionWrapper.class}),
    SELECT_OBJS_BY_WRAPPER("selectObjs", ExecuteMethod.SELECT, new Class<?>[]{ConditionWrapper.class}),
    SELECT_OBJ_BY_WRAPPER("selectObj", ExecuteMethod.SELECT, new Class<?>[]{ConditionWrapper.class}),
    SELECT_ONE_MAP_BY_WRAPPER("selectOneMap", ExecuteMethod.SELECT, new Class<?>[]{ConditionWrapper.class}),
    SELECT_LIST_MAP_BY_WRAPPER("selectListMap", ExecuteMethod.SELECT, new Class<?>[]{ConditionWrapper.class}),
    SELECT_PAGE_MAP_BY_WRAPPER("selectPageMap", ExecuteMethod.SELECT, new Class<?>[]{ConditionWrapper.class}),
    SELECT_MAP_BY_WRAPPER("selectMap", ExecuteMethod.SELECT, new Class<?>[]{ConditionWrapper.class, Class.class, Class.class}),
    SELECT_ARRAYS("selectArrays", ExecuteMethod.SELECT, new Class<?>[]{Class.class, String.class, Object[].class}),
    SELECT_ONE_BY_ENTITY("selectOne", ExecuteMethod.SELECT, new Class<?>[]{Object.class}),
    SELECT_LIST_BY_ENTITY("selectList", ExecuteMethod.SELECT, new Class<?>[]{Object.class}),
    SELECT_PAGE_BY_ENTITY("selectPage", ExecuteMethod.SELECT, new Class<?>[]{Object.class, DbPageRows.class}),
    SELECT_LIST_BY_SYNC("selectList", ExecuteMethod.SELECT, new Class<?>[]{SyncQueryWrapper.class}),
    SELECT_ONE_BY_SYNC("selectOne", ExecuteMethod.SELECT, new Class<?>[]{SyncQueryWrapper.class}),
    SELECT_PAGE_BY_SYNC("selectPage", ExecuteMethod.SELECT, new Class<?>[]{SyncQueryWrapper.class}),


    DELETE_BY_KEY("deleteByKey", ExecuteMethod.DELETE, new Class<?>[]{Class.class, Serializable.class}),
    DELETE_BATCH_KEYS("deleteBatchKeys", ExecuteMethod.DELETE, new Class<?>[]{Class.class, Serializable.class}),
    DELETE_BY_CONDITION("deleteByCondition", ExecuteMethod.DELETE, new Class<?>[]{Class.class, String.class, Object[].class}),
    DELETE_SELECTIVE("deleteSelective", ExecuteMethod.DELETE, new Class<?>[]{ConditionWrapper.class}),

    INSERT_ONE("insert", ExecuteMethod.INSERT, new Class<?>[]{Object.class}),
    INSERT_BATCH("insertBatch", ExecuteMethod.INSERT, new Class<?>[]{Collection.class}),

    UPDATE_BY_KEY("updateByKey", ExecuteMethod.UPDATE, new Class<?>[]{Object.class}),
    UPDATE_SELECTIVE_BY_WRAPPER("updateSelective", ExecuteMethod.UPDATE, new Class<?>[]{Object.class, ConditionWrapper.class}),
    UPDATE_BY_CONDITION("updateByCondition", ExecuteMethod.UPDATE, new Class<?>[]{Object.class, String.class, Object[].class}),
    UPDATE_SELECTIVE_BY_SQL_SET("updateSelective", ExecuteMethod.UPDATE, new Class<?>[]{AbstractUpdateSet.class}),

    SAVE("save", ExecuteMethod.OTHERS, new Class[]{Object.class}),
    EXECUTE_SQL("executeSql", ExecuteMethod.OTHERS, new Class<?>[]{String.class, Object[].class}),
    DROP_TABLES("dropTables", ExecuteMethod.OTHERS, new Class<?>[]{Class[].class}),
    CREATE_TABLES("createTables", ExecuteMethod.OTHERS, new Class<?>[]{Class[].class}),
    EXEC_TRANS("execTrans", ExecuteMethod.OTHERS, new Class<?>[]{TransactionExecutor.class}),
    CREATE_CHAIN("createChain", ExecuteMethod.OTHERS, new Class<?>[]{Class.class}),

    ;


    /**
     * 方法名称
     */
    private final String method;
    /**
     * 参数数量
     */
    private final ExecuteMethod executeMethod;
    /**
     * 参数类型数组
     */
    private final Class<?>[] typeArr;

    MethodKind(String method, ExecuteMethod executeMethod, Class<?>[] typeArr) {
        this.method = method;
        this.executeMethod = executeMethod;
        this.typeArr = typeArr;
    }

    public String getMethod() {
        return method;
    }

    public ExecuteMethod getExecuteMethod() {
        return executeMethod;
    }

    public Class<?>[] getTypeArr() {
        return typeArr;
    }
}
