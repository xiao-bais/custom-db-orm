package com.custom.proxy;

import com.custom.annotations.DbTable;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.enums.ExecuteMethod;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;
import com.custom.annotations.check.CheckExecute;
import com.custom.wrapper.ConditionEntity;
import com.custom.wrapper.ConditionWrapper;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/17 9:55
 * @Desc：在执行之前做一些必要的检查，以减少异常的出现
 **/
@SuppressWarnings("unchecked")
public class SqlParamsCheckProxy<T> implements MethodInterceptor {

    private final T obj;

    private final DbDataSource dbDataSource;

    private final DbCustomStrategy dbCustomStrategy;

    public SqlParamsCheckProxy(T obj, DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        this.obj = obj;
        this.dbDataSource = dbDataSource;
        this.dbCustomStrategy = dbCustomStrategy;
    }


    public T createProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(obj.getClass());
        enhancer.setCallback(this);
        return (T) enhancer.create(new Class[]{DbDataSource.class, DbCustomStrategy.class}, new Object[]{dbDataSource, dbCustomStrategy});
    }


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        CheckExecute annotation = method.getAnnotation(CheckExecute.class);
        if(Objects.isNull(annotation)) {
            return methodProxy.invokeSuper(o, objects);
        }
        if(JudgeUtilsAx.isEmpty(objects[0])) throw new NullPointerException();

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
        if(objects[0] instanceof List) {
            insertParam = ((List<Object>) objects[0]).get(0);
        }
        if(!insertParam.getClass().isAnnotationPresent(DbTable.class)) {
            throw new CustomCheckException(ExceptionConst.EX_DBTABLE__NOTFOUND + insertParam.getClass().getName());
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
            if(objects[0] instanceof ConditionWrapper && JudgeUtilsAx.isEmpty(((ConditionWrapper<?>) objects[0]).getFinalConditional())) {
                throw new CustomCheckException("delete condition cannot be empty");
            }
            return;
        }
        Object deleteParam = objects[1];
        if(!((Class<?>)objects[0]).isAnnotationPresent(DbTable.class)) {
            throw new CustomCheckException(ExceptionConst.EX_DBTABLE__NOTFOUND + objects[0].getClass().getName());
        }
        if(JudgeUtilsAx.isEmpty(deleteParam)) {
            throw new CustomCheckException("delete condition cannot be empty");
        }
    }

    /**
    * 修改的时候做参数的预检查
    */
    private void update(Object[] objects, Method method) {
        if(!objects[0].getClass().isAnnotationPresent(DbTable.class)) {
            throw new CustomCheckException(ExceptionConst.EX_DBTABLE__NOTFOUND + objects[0].getClass().getName());
        }
        if(!CustomUtil.isKeyTag(objects[0].getClass()) && method.getName().equals("updateByKey")) {
            throw new CustomCheckException(ExceptionConst.EX_DBKEY_NOTFOUND + objects[0].getClass().getName());
        }
        if(method.getName().equals("updateByCondition")) {
            if(JudgeUtilsAx.isEmpty(objects[0])) {
                throw new CustomCheckException("update entity cannot be null");
            }
            if(JudgeUtilsAx.isEmpty(objects[1])) {
                throw new CustomCheckException("update condition cannot be empty");
            }
            if (JudgeUtilsAx.isNotEmpty(objects[1])) {
                ConditionEntity<Object> conditionEntity = (ConditionEntity<Object>) objects[1];
                if(JudgeUtilsAx.isEmpty(conditionEntity.getFinalConditional())) {
                    throw new CustomCheckException("update condition cannot be empty");
                }
            }
        }
    }

    /**
    * 查询的时候做参数的预检查
    */
    private void select(Object[] objects, Method method) {
        if(!((Class<?>)objects[0]).isAnnotationPresent(DbTable.class)) {
            throw new CustomCheckException(ExceptionConst.EX_DBTABLE__NOTFOUND + objects[0].getClass().getName());
        }
        String methodName = method.getName();
        if(JudgeUtilsAx.isEmpty(objects[1])) {
            switch (methodName) {
                case "selectOneByKey":
                case "selectBatchByKeys":
                    throw new CustomCheckException(ExceptionConst.EX_PRIMARY_KEY_NOT_SPECIFIED);

                case "selectOneBySql":
                case "selectBySql":
                    throw new CustomCheckException(ExceptionConst.EX_SQL_NOT_EMPTY);

                case "selectOneByCondition":
                    throw new CustomCheckException("condition cannot be empty");
            }
        }
    }
}
