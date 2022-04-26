package com.custom.action.proxy;

import com.custom.action.util.DbUtil;
import com.custom.action.wrapper.ConditionWrapper;
import com.custom.comm.JudgeUtilsAx;
import com.custom.comm.annotations.DbTable;
import com.custom.comm.annotations.check.CheckExecute;
import com.custom.comm.enums.ExecuteMethod;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.comm.exceptions.ExceptionConst;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
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
        if(JudgeUtilsAx.isEmpty(objects[0])) {
            ExThrowsUtil.toNull("实体对象空掉了");
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
                ExThrowsUtil.toCustom("delete condition cannot be empty");
            }
            if(objects[0] instanceof ConditionWrapper && JudgeUtilsAx.isEmpty(((ConditionWrapper<?>) objects[0]).getFinalConditional())) {
                ExThrowsUtil.toCustom("delete condition cannot be empty");
            }
            return;
        }
        Object deleteParam = objects[1];
        if(!((Class<?>)objects[0]).isAnnotationPresent(DbTable.class)) {
            ExThrowsUtil.toCustom("@DbTable not found in class "+ objects[0].getClass());
        }
        if(JudgeUtilsAx.isEmpty(deleteParam)) {
            ExThrowsUtil.toCustom("delete condition cannot be empty");
        }
    }

    /**
    * 修改的时候做参数的预检查
    */
    private void update(Object[] objects, Method method) {
        if(Objects.isNull(objects[0])) {
            ExThrowsUtil.toNull("update entity cannot be null");
        }
        if(!objects[0].getClass().isAnnotationPresent(DbTable.class)) {
            ExThrowsUtil.toCustom("@DbTable not found in class " + objects[0].getClass());
        }
        if(!DbUtil.isKeyTag(objects[0].getClass()) && method.getName().equals("updateByKey")) {
            ExThrowsUtil.toCustom("@DbKey was not found in class " + objects[0].getClass());
        }
        if(method.getName().equals("updateByCondition") && (JudgeUtilsAx.isEmpty(objects[1]) || JudgeUtilsAx.isEmpty(((ConditionWrapper<?>) objects[1]).getFinalConditional()))) {
            ExThrowsUtil.toCustom("update condition cannot be empty");
        }
    }

    /**
    * 查询的时候做参数的预检查
    */
    private void select(Object[] objects, Method method) {

        if (objects[0] instanceof ConditionWrapper) {
            if (Objects.isNull(((ConditionWrapper<T>) objects[0]).getEntityClass())) {
                ExThrowsUtil.toCustom("实体Class对象不能为空");
            }
            return;
        }
        if (!((Class<?>) objects[0]).isAnnotationPresent(DbTable.class)) {
            ExThrowsUtil.toCustom("@DbTable not found in class " + objects[0].getClass());
        }
        String methodName = method.getName();
        if(JudgeUtilsAx.isEmpty(objects[1])) {
            switch (methodName) {
                case "selectOneByKey":
                case "selectBatchByKeys":
                    ExThrowsUtil.toCustom("The value of the primary key is not specified when querying based on the primary key");

                case "selectOneBySql":
                case "selectBySql":
                    ExThrowsUtil.toCustom("The Sql to be Not Empty");

                case "selectOneByCondition":
                    ExThrowsUtil.toCustom("condition cannot be empty");
            }
        }
    }
}
