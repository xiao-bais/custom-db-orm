package com.custom.action.service;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.interfaces.TableExecutor;
import com.custom.comm.page.DbPageRows;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Xiao-Bai
 * @date 2023/2/8 13:21
 */
public class DbQueryWrapper<T> {

    private final ConditionWrapper<T> wrapper;
    private final TableExecutor<T, Serializable> tableExecutor;

    private DbQueryWrapper(ConditionWrapper<T> wrapper, TableExecutor<T, Serializable> tableExecutor) {
        this.wrapper = wrapper;
        this.tableExecutor = tableExecutor;
    }

    public static <T> DbQueryWrapper<T> build(ConditionWrapper<T> wrapper, TableExecutor<T, Serializable> tableExecutor) {
        return new DbQueryWrapper<>(wrapper, tableExecutor);
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


    public T getOneOpt(Supplier<T> supplier) throws Exception {
        return Optional.ofNullable(getOne()).orElseGet(supplier);
    }


    public long count() throws Exception {
        return tableExecutor.selectCount(wrapper);
    }


    public Object getObj() throws Exception {
        return tableExecutor.selectObj(wrapper);
    }


    public Object getObjOpt(Supplier<T> supplier) throws Exception {
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

}
