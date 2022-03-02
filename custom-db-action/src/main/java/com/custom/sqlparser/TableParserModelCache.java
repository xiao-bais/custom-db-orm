package com.custom.sqlparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2022/2/24 11:14
 * @Desc：实体解析模板缓存
 **/
@SuppressWarnings("unchecked")
public class TableParserModelCache {

    private static final Logger logger = LoggerFactory.getLogger(TableParserModelCache.class);

    /**
     * 实体解析模板缓存
     */
    private Map<String, Object> tableModel;

//    /**
//     * 执行方法解析模板缓存-主键缓存
//     */
//    private Map<String, Object> executeKeyModel;
//
//    /**
//     * 执行方法解析模板缓存-字段缓存
//     */
//    private Map<String, Object> executeFieldModel;
//
//    /**
//     * 执行方法解析模板缓存-关联缓存(related)
//     */
//    private Map<String, Object> executeRelatedModel;
//
//    /**
//     * 执行方法解析模板缓存-关联缓存（joinTables）
//     */
//    private Map<String, List<String>> executeJoinTableMap;
//
//    /**
//     * 执行方法解析模板缓存-关联缓存（joinTables-DbMap）
//     */
//    private Map<String, Map<String, String>> executeDbMap;


    public TableParserModelCache(int size) {
        initialize(size);
    }

    private void initialize(int size) {
        logger.info("Entity parse model Initialized Started ... ...");
        this.tableModel = new CustomLocalCache(size);
//        this.executeKeyModel = new CustomLocalCache(size);
//        this.executeFieldModel = new CustomLocalCache(size);
//        this.executeRelatedModel = new CustomLocalCache(size);
//        this.executeJoinTableMap = new ConcurrentHashMap<>(size);
//        this.executeDbMap = new ConcurrentHashMap<>(size);
    }

    public void setTableModel(String key, Object val) {
        this.tableModel.put(key, val);
    }

    public <T> TableSqlBuilder<T> getTableModel(String key) {
        return (TableSqlBuilder<T>) tableModel.get(key);
    }
}
