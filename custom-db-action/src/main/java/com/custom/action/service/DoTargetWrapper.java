package com.custom.action.service;

import com.custom.action.condition.*;
import com.custom.action.interfaces.TableExecutor;
import com.custom.comm.page.DbPageRows;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 执行目标包装类，传入查询，删除，修改的条件(wrapper)后，执行指定的操作
 * @author   Xiao-Bai
 * @since 2023/2/8 13:21
 */
public class DoTargetWrapper<T> {

    private final ConditionWrapper<T> wrapper;
    private final TableExecutor<T, Serializable> tableExecutor;

    private DoTargetWrapper(ConditionWrapper<T> wrapper, TableExecutor<T, Serializable> tableExecutor) {
        this.wrapper = wrapper;
        this.tableExecutor = tableExecutor;
    }

    public static <T> DoTargetWrapper<T> build(ConditionWrapper<T> wrapper, TableExecutor<T, Serializable> tableExecutor) {
        return new DoTargetWrapper<>(wrapper, tableExecutor);
    }

    public List<T> getList() throws Exception {
       return tableExecutor.selectList(wrapper);
    }

    public Stream<T> getListStream() throws Exception {
        return tableExecutor.selectList(wrapper).stream();
    }


    public T getOne() throws Exception {
        return tableExecutor.selectOne(wrapper);
    }


    public T getOne(Supplier<T> supplier) throws Exception {
        return Optional.ofNullable(getOne()).orElseGet(supplier);
    }


    public long count() throws Exception {
        return tableExecutor.selectCount(wrapper);
    }


    public Object getObj() throws Exception {
        return tableExecutor.selectObj(wrapper);
    }


    public Object getObj(Supplier<T> supplier) throws Exception {
        return Optional.ofNullable(getObj()).orElseGet(supplier);
    }


    public List<Object> getObjs() throws Exception {
        return tableExecutor.selectObjs(wrapper);
    }

    public Stream<Object> getObjsStream() throws Exception {
        return tableExecutor.selectObjs(wrapper).stream();
    }

    public <V> List<V> getObjsStream(Function<? super Object, V> convert) throws Exception {
        return tableExecutor.selectObjs(wrapper).stream().filter(Objects::nonNull).map(convert).collect(Collectors.toList());
    }

    public Map<String, Object> getMap() throws Exception {
        return tableExecutor.selectMap(wrapper);
    }


    public List<Map<String, Object>> getMaps() throws Exception {
        return tableExecutor.selectMaps(wrapper);
    }


    public DbPageRows<T> getPage() throws Exception {
        return tableExecutor.selectPage(wrapper);
    }

    public boolean delete() throws Exception {
        return tableExecutor.deleteSelective(wrapper) > 0;
    }

    /**
     * 默认的sql set 设置器
     */
    public boolean updateSet(Consumer<DefaultUpdateSetSqlSetter<T>> consumer) throws Exception {
        if (wrapper instanceof LambdaConditionWrapper) {
            throw new IllegalArgumentException("当前方法不允许存在lambda表达式的条件构造器，请换成 " + DefaultConditionWrapper.class.getName() + "类型的构造器");
        }
        DefaultUpdateSetSqlSetter<T> sqlSetter = new DefaultUpdateSetSqlSetter<>(wrapper.getEntityClass());
        consumer.accept(sqlSetter);
        DefaultUpdateSet<T> updateSet = Conditions.update(wrapper.getEntityClass());
        updateSet.setter(sqlSetter);
        updateSet.where((DefaultConditionWrapper<T>) wrapper);
        return tableExecutor.updateSelective(updateSet) > 0;
    }

    /**
     * lambda表达式的sql set 设置器
     */
    public boolean updateExSet(Consumer<LambdaUpdateSetSqlSetter<T>> consumer) throws Exception {
        if (wrapper instanceof DefaultConditionWrapper) {
            throw new IllegalArgumentException("当前方法不允许存在default表达式的条件构造器，请换成 " + LambdaConditionWrapper.class.getName() + "类型的构造器");
        }
        LambdaUpdateSetSqlSetter<T> sqlSetter = new LambdaUpdateSetSqlSetter<>(wrapper.getEntityClass());
        consumer.accept(sqlSetter);
        LambdaUpdateSet<T> updateSet = Conditions.lambdaUpdate(wrapper.getEntityClass());
        updateSet.setter(sqlSetter);
        updateSet.where((LambdaConditionWrapper<T>) wrapper);
        return tableExecutor.updateSelective(updateSet) > 0;
    }


}
