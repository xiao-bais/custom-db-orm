package com.custom.sqlparser;

import com.custom.dbconfig.CustomLocalCache;
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

    private static Logger logger = LoggerFactory.getLogger(TableParserModelCache.class);

    /**
     * 实体解析模板缓存
     */
    private Map<String, Object> tableModel;

    /**
     * 执行方法解析模板缓存-主键缓存
     */
    private Map<String, Object> executeKeyModel;

    /**
     * 执行方法解析模板缓存-字段缓存
     */
    private Map<String, Object> executeFieldModel;

    /**
     * 执行方法解析模板缓存-关联缓存
     */
    private Map<String, Object> executeRelatedModel;


    public TableParserModelCache(int size) {
        initialize(size);
    }

    private void initialize(int size) {
        logger.info("table parse model initialized ... ...");
        this.tableModel = new CustomLocalCache(size);
        this.executeKeyModel = new CustomLocalCache(size);
        this.executeFieldModel = new CustomLocalCache(size);
        this.executeRelatedModel = new CustomLocalCache(size);
    }

    public TableParserModelCache setTableModel(String key, Object val) {
        this.tableModel.put(key, val);
        return this;
    }

    public TableParserModelCache setKeyModel(String key, Object val) {
        this.executeKeyModel.put(key, val);
        return this;
    }

    public TableParserModelCache setFieldModel(String key, Object val) {
        this.executeFieldModel.put(key, val);
        return this;
    }

    public TableParserModelCache setRelatedModel(String key, Object val) {
        this.executeRelatedModel.put(key, val);
        return this;
    }

    public <T> TableSqlBuilder<T> getTableModel(String key) {
        return (TableSqlBuilder<T>) tableModel.get(key);
    }

    public <T> DbKeyParserModel<T> getKeyModel(String key) {
        return (DbKeyParserModel<T>) executeKeyModel.get(key);
    }

    public <T> List<DbFieldParserModel<T>> getFieldModel(String key) {
        return (List<DbFieldParserModel<T>>) executeFieldModel.get(key);
    }

    public <T> List<DbRelationParserModel<T>> getRelatedModel(String key) {
        return (List<DbRelationParserModel<T>>) executeRelatedModel.get(key);
    }




}
