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


    public TableParserModelCache(int size) {
        initialize(size);
    }

    public TableParserModelCache() {
        initialize();
    }

    private void initialize(int size) {
        logger.info("Entity parse model Initialized Started ... ...");
        this.tableModel = new CustomLocalCache(size);
    }

    private void initialize() {
        this.tableModel = new CustomLocalCache();
    }

    public void setTableModel(String key, Object val) {
        this.tableModel.put(key, val);
    }

    public <T> TableSqlBuilder<T> getTableModel(String key) {
        return (TableSqlBuilder<T>) tableModel.get(key);
    }
}
