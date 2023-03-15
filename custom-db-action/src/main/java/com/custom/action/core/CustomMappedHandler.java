package com.custom.action.core;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.methods.MethodKind;
import com.custom.action.core.methods.delete.DeleteBatchKeys;
import com.custom.action.core.methods.delete.DeleteByCondition;
import com.custom.action.core.methods.delete.DeleteByKey;
import com.custom.action.core.methods.delete.DeleteSelective;
import com.custom.action.core.methods.insert.InsertOne;
import com.custom.action.core.methods.insert.InsertBatch;
import com.custom.action.core.methods.others.*;
import com.custom.action.core.methods.select.*;
import com.custom.action.core.methods.update.UpdateByCondition;
import com.custom.action.core.methods.update.UpdateByKey;
import com.custom.action.core.methods.update.UpdateSelectiveBySqlSet;
import com.custom.action.core.methods.update.UpdateSelectiveByWrapper;
import com.custom.action.interfaces.ExecuteHandler;
import com.custom.comm.enums.ExecuteMethod;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.ReflectUtil;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.configuration.DbGlobalConfig;
import com.custom.jdbc.executor.JdbcExecutorFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Xiao-Bai
 * @since 2023/3/9 19:32
 */
public class CustomMappedHandler {

    /**
     * 处理执行目标方法
     */
    public <T> Object handleExecute(Method method, Object[] params) throws Exception {
        ExecuteHandler executor = METHOD_HANDLER_CACHE.get(method);
        if (executor == null) {
            MethodKind methodKind = this.findMethod(method);
            executor = ReflectUtil.getInstance(EXECUTE_HANDLER_CACHE.get(methodKind));
            METHOD_HANDLER_CACHE.put(method, executor);
        }
        return handleExecute(executor.getKind(), params);
    }

    /**
     * 处理目标执行方法
     */
    public <T> Object handleExecute(MethodKind methodKind, Object... params) throws Exception {
        ExecuteHandler executor = ReflectUtil.getInstance(EXECUTE_HANDLER_CACHE.get(methodKind));
        Class<T> mappedType = executor.getMappedType(params);
        Object result = executor.doExecute(executorFactory, mappedType, params);
        // if save then insert or update....
        if (executor.getKind() == MethodKind.SAVE) {
            MethodKind saveKind = (MethodKind) executor.doExecute(executorFactory, mappedType, params);
            executor = ReflectUtil.getInstance(EXECUTE_HANDLER_CACHE.get(saveKind));
            result = executor.doExecute(executorFactory, mappedType, params);
        }
        // query after do something
        if (executor.getKind().getExecuteMethod() == ExecuteMethod.SELECT) {
            executorFactory.queryAfterHandle(mappedType, result);
        }
        return result;
    }


    /**
     * 查找执行方法
     */
    private MethodKind findMethod(Method method) {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        return Arrays.stream(MethodKind.values())
                .filter(e -> e.getMethod().equals(methodName))
                .filter(e -> {
                    Class<?>[] typeArr = e.getTypeArr();
                    if (typeArr.length != parameterTypes.length) {
                        return false;
                    }
                    // 判断两者的参数类型一一对应
                    int targetIndex = 0;
                    int len = parameterTypes.length;
                    for (int i = 0; i < len; i++) {
                        Class<?> targetClass = typeArr[i];
                        Class<?> thisClass;
                        if (parameterTypes[i] == null) {
                            thisClass = targetClass;
                        } else {
                            thisClass = typeArr[i];
                        }
                        if (ConditionWrapper.class.isAssignableFrom(thisClass)) {
                            return !Object.class.equals(targetClass);
                        }
                        // target 目标参数类型
                        // this 本次传递的参数类型
                        if (targetClass.isAssignableFrom(thisClass)) {
                            targetIndex++;
                        } else if (targetClass.isPrimitive()) {
                            Class<?> primitiveClass = PRIMITIVE_MAPPED.get(targetClass);
                            if (thisClass.equals(primitiveClass)) {
                                targetIndex++;
                            }
                        }
                    }
                    return targetIndex == len;
                }).findFirst().orElseThrow(() ->
                        new CustomCheckException("Unknown execution method : " + methodName)
                );
    }


    private final static Map<Method, ExecuteHandler> METHOD_HANDLER_CACHE = new ConcurrentHashMap<>();
    private final JdbcExecutorFactory executorFactory;
    private final static Map<Class<?>, Class<?>> PRIMITIVE_MAPPED = new HashMap<>(8);
    private final static Map<MethodKind, Class<? extends ExecuteHandler>> EXECUTE_HANDLER_CACHE = new ConcurrentHashMap<>();

    public CustomMappedHandler(DbDataSource dbDataSource, DbGlobalConfig dbGlobalConfig) {
        this.executorFactory = new JdbcExecutorFactory(dbDataSource, dbGlobalConfig);
    }

    public JdbcExecutorFactory getExecutorFactory() {
        return executorFactory;
    }

    static {
        PRIMITIVE_MAPPED.put(Integer.TYPE, Integer.class);
        PRIMITIVE_MAPPED.put(Long.TYPE, Long.class);
        PRIMITIVE_MAPPED.put(Short.TYPE, Short.class);
        PRIMITIVE_MAPPED.put(Double.TYPE, Double.class);
        PRIMITIVE_MAPPED.put(Float.TYPE, Float.class);
        PRIMITIVE_MAPPED.put(Byte.TYPE, Byte.class);
        PRIMITIVE_MAPPED.put(Character.TYPE, Character.class);
        PRIMITIVE_MAPPED.put(Boolean.TYPE, Boolean.class);

        // select 23.
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_ARRAYS, SelectArrays.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_BATCH_KEYS, SelectBatchKeys.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_BY_KEY, SelectByKey.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_COUNT_BY_WRAPPER, SelectCountByWrapper.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_LIST, SelectList.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_LIST_BY_ENTITY, SelectListByEntity.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_LIST_BY_SQL, SelectListBySql.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_LIST_BY_WRAPPER, SelectListByWrapper.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_LIST_MAP_BY_WRAPPER, SelectListMapByWrapper.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_MAP, SelectMap.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_MAP_BY_WRAPPER, SelectMapByWrapper.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_OBJ_BY_SQL, SelectObjBySql.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_OBJ_BY_WRAPPER, SelectObjByWrapper.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_OBJS_BY_WRAPPER, SelectObjsByWrapper.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_ONE, SelectOne.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_ONE_BY_ENTITY, SelectOneByEntity.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_ONE_BY_SQL, SelectOneBySql.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_ONE_BY_WRAPPER, SelectOneByWrapper.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_ONE_MAP_BY_WRAPPER, SelectOneMapByWrapper.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_PAGE, SelectPage.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_PAGE_BY_ENTITY, SelectPageByEntity.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_PAGE_BY_WRAPPER, SelectPageByWrapper.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SELECT_PAGE_MAP_BY_WRAPPER, SelectPageMapByWrapper.class);

        // delete 4.
        EXECUTE_HANDLER_CACHE.put(MethodKind.DELETE_BY_KEY, DeleteByKey.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.DELETE_BATCH_KEYS, DeleteBatchKeys.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.DELETE_BY_CONDITION, DeleteByCondition.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.DELETE_SELECTIVE, DeleteSelective.class);

        // insert 2.
        EXECUTE_HANDLER_CACHE.put(MethodKind.INSERT_ONE, InsertOne.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.INSERT_BATCH, InsertBatch.class);

        // update 4.
        EXECUTE_HANDLER_CACHE.put(MethodKind.UPDATE_BY_KEY, UpdateByKey.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.UPDATE_BY_CONDITION, UpdateByCondition.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.UPDATE_SELECTIVE_BY_WRAPPER, UpdateSelectiveByWrapper.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.UPDATE_SELECTIVE_BY_SQL_SET, UpdateSelectiveBySqlSet.class);

        // other
        EXECUTE_HANDLER_CACHE.put(MethodKind.CREATE_TABLES, CreateTables.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.DROP_TABLES, DropTables.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.EXEC_TRANS, ExecTrans.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.SAVE, Save.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.EXECUTE_SQL, ExecuteSql.class);
        EXECUTE_HANDLER_CACHE.put(MethodKind.CREATE_CHAIN, CreateChain.class);

    }
}
