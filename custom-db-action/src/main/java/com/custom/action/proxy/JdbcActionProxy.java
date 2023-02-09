package com.custom.action.proxy;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.condition.ConditionWrapper;
import com.custom.action.condition.UpdateSetWrapper;
import com.custom.action.core.JdbcAction;
import com.custom.action.core.SqlExecutor;
import com.custom.action.util.DbUtil;
import com.custom.comm.annotations.DbTable;
import com.custom.comm.annotations.check.CheckExecute;
import com.custom.comm.enums.ExecuteMethod;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.JudgeUtil;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.exceptions.SQLSessionException;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * 在执行之前做一些必要的检查，以减少异常的出现
 * @author  Xiao-Bai
 * @since  2021/11/17 9:55
 **/
@SuppressWarnings("unchecked")
public class JdbcActionProxy implements MethodInterceptor {

    private final SqlExecutor sqlExecutor;

    private final DbDataSource dbDataSource;

    private final DbCustomStrategy dbCustomStrategy;

    public JdbcActionProxy(SqlExecutor sqlExecutor, DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        this.sqlExecutor = sqlExecutor;
        this.dbDataSource = dbDataSource;
        this.dbCustomStrategy = dbCustomStrategy;
    }


    public JdbcAction createProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(sqlExecutor.getClass());
        enhancer.setCallback(this);
        try {
            return (JdbcAction) enhancer.create(new Class[]{DbDataSource.class, DbCustomStrategy.class}
                    , new Object[]{dbDataSource, dbCustomStrategy});
        } catch (Exception e) {
            Throwable throwable = e.getCause();
            if (throwable instanceof SQLSessionException) {
                throw (SQLSessionException) e.getCause();
            }
            if (throwable instanceof CustomCheckException) {
                throw (CustomCheckException) e.getCause();
            }
            throw e;
        }

    }


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        CheckExecute annotation = method.getAnnotation(CheckExecute.class);
        if(Objects.isNull(annotation)) {
            return methodProxy.invokeSuper(o, objects);
        }
        if(JudgeUtil.isEmpty(objects[0])) {
            throw new NullPointerException("Execution parameter cannot be empty");
        }

        ExecuteMethod target = annotation.target();
        switch (target) {
            // 对当前执行方法进行参数的预检查
            case INSERT:
                this.insert(objects);
                break;
            case DELETE:
                this.delete(objects);
                break;
            case UPDATE:
                this.update(objects, method);
                break;
            case SELECT:
                this.select(objects, method);
            case NONE: break;
        }
        return methodProxy.invokeSuper(o, objects);
    }



    /**
    * 添加的时候做参数的预检查
    */
    private void insert(Object[] objects) {
        Object insertParam = objects[0];
        if(List.class.isAssignableFrom(objects[0].getClass())) {
            insertParam = ((List<Object>) objects[0]).get(0);
        }
        if(!insertParam.getClass().isAnnotationPresent(DbTable.class)) {
            throw new CustomCheckException("@DbTable not found in class " + insertParam.getClass());
            
        }
    }

    /**
    * 删除的时候做参数的预检查
    */
    private void delete(Object[] objects) {
        int length = objects.length;
        if(length == 1) {
            if(Objects.isNull(objects[0])) {
                throw new CustomCheckException("delete condition cannot be empty");
            }
            if(objects[0] instanceof ConditionWrapper && JudgeUtil.isEmpty(((ConditionWrapper<?>) objects[0]).getFinalConditional())) {
                throw new CustomCheckException("delete condition cannot be empty");
            }
            return;
        }
        Object deleteParam = objects[1];
        if(!((Class<?>)objects[0]).isAnnotationPresent(DbTable.class)) {
            throw new CustomCheckException("@DbTable not found in class "+ objects[0].getClass());
        }
        if(JudgeUtil.isEmpty(deleteParam)) {
            throw new CustomCheckException("delete condition cannot be empty");
        }
    }

    /**
    * 修改的时候做参数的预检查
    */
    private void update(Object[] objects, Method method) {
        String methodName = method.getName();
        if(Objects.isNull(objects[0])) {
            if (methodName.equals("updateSelective")) {
                throw new NullPointerException("Update setter cannot be empty");
            }
            throw new NullPointerException("Update entity cannot be null");
        }
        if (methodName.equals("updateSelective")) {
            try {
                AbstractUpdateSet<?> updateSet = (AbstractUpdateSet<?>) objects[0];
                UpdateSetWrapper<?> updateSetWrapper = updateSet.getUpdateSetWrapper();
                ConditionWrapper<?> conditionWrapper = updateSet.getConditionWrapper();
                if (updateSetWrapper == null || JudgeUtil.isEmpty(updateSetWrapper.getSqlSetter())) {
                    throw new CustomCheckException("Set value cannot be empty");
                }
                if (conditionWrapper == null || JudgeUtil.isEmpty(conditionWrapper.getFinalConditional())) {
                    throw new CustomCheckException("Update condition cannot be empty");
                }
            }catch (ClassCastException e) {
                Object entity = objects[0];
                Asserts.notNull(entity, "update entity cannot ba empty");
                ConditionWrapper<?> conditionWrapper = (ConditionWrapper<?>) objects[1];
                Asserts.notNull(conditionWrapper, "Update condition cannot be empty");
                Asserts.notEmpty(conditionWrapper.getFinalConditional(), "Update condition cannot be empty");
            }
            return;
        }
        if(!objects[0].getClass().isAnnotationPresent(DbTable.class)) {
            throw new CustomCheckException("@DbTable not found in class " + objects[0].getClass());
        }
        if(!DbUtil.hasPriKey(objects[0].getClass()) && methodName.equals("updateByKey")) {
            throw new CustomCheckException("@DbKey was not found in class " + objects[0].getClass());
        }
        if(methodName.equals("updateByCondition") && (JudgeUtil.isEmpty(objects[1]))) {
            throw new CustomCheckException("update condition cannot be empty");
        }
    }

    /**
    * 查询的时候做参数的预检查
    */
    private void select(Object[] objects, Method method) {

        if (objects[0] instanceof ConditionWrapper) {
            if (Objects.isNull(((ConditionWrapper<?>) objects[0]).getEntityClass())) {
                throw new CustomCheckException("Entity class object cannot be empty");
            }
            return;
        }
        Class<?> targetClass = objects[0].getClass();
        if (Class.class.equals(targetClass)) {
            targetClass = (Class<?>) objects[0];
        }
        if (!targetClass.isAnnotationPresent(DbTable.class)) {
            throw new CustomCheckException("@DbTable not found in " + targetClass);
        }
        if(objects.length == 1) {
            return;
        }
        String methodName = method.getName();
        if(JudgeUtil.isEmpty(objects[1])) {
            switch (methodName) {
                case "selectOneByKey":
                case "selectBatchByKeys":
                    throw new CustomCheckException("The value of the primary key is not specified when querying based on the primary key");

                case "selectOneBySql":
                case "selectBySql":
                    throw new CustomCheckException("The Sql to be Not Empty");

                case "selectOne":
                    throw new CustomCheckException("condition cannot be empty");
            }
        }
    }
}
