package com.custom.action.fieldfill;

import com.custom.comm.enums.FillStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/21 14:47
 * @Desc：自动填充对象
 **/
public class TableFillObject {

    private Class<?> entityClass;

    /**
     * 当字段未找到时，是否抛出异常？
     */
    private Boolean notFoundFieldThrowException = false;

    /**
     * 自动填充字段与值的映射
     * key-java属性字段
     * value-指定填充的值
     */
    private Map<String, Object> tableFillMapper;

    /**
     * 自动填充的策略
     */
    private FillStrategy strategy = FillStrategy.DEFAULT;


    public TableFillObject(Class<?> entityClass, Boolean notFoundFieldThrowException, FillStrategy strategy, Map<String, Object> tableFillMapper) {
        this.entityClass = entityClass;
        this.notFoundFieldThrowException = notFoundFieldThrowException;
        this.strategy = strategy;
        this.tableFillMapper = tableFillMapper;
    }

    public TableFillObject() {}

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public Boolean getNotFoundFieldThrowException() {
        return notFoundFieldThrowException;
    }

    public void setNotFoundFieldThrowException(Boolean notFoundFieldThrowException) {
        this.notFoundFieldThrowException = notFoundFieldThrowException;
    }

    public Map<String, Object> getTableFillMapper() {
        if(tableFillMapper == null) {
            tableFillMapper = new HashMap<>();
        }
        return tableFillMapper;
    }

    public TableFillObject addField(String javaField, Object val) {
        if(tableFillMapper == null) {
            tableFillMapper = new HashMap<>();
        }
        tableFillMapper.put(javaField, val);
        return this;
    }


    public void setTableFillMapper(Map<String, Object> tableFillMapper) {
        this.tableFillMapper = tableFillMapper;
    }

    public FillStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(FillStrategy strategy) {
        this.strategy = strategy;
    }
}
