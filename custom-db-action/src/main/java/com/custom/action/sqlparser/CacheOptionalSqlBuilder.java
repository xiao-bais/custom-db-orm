package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractSqlBuilder;

/**
 * @author Xiao-Bai
 * @date 2022/9/22 1:15
 * @desc
 */
public class CacheOptionalSqlBuilder<T> {

    /**
     * 查询sql构造模板对象
     */
    private final AbstractSqlBuilder<T> selectSqlBuilder;
    private boolean selectInit;
    /**
     * 插入sql构造模板对象
     */
    private final AbstractSqlBuilder<T> insertSqlBuilder;
    private boolean insertInit;
    /**
     * 修改sql构造模板对象
     */
    private final AbstractSqlBuilder<T> updateSqlBuilder;
    private boolean updateInit;
    /**
     * 删除sql构造模板对象
     */
    private final AbstractSqlBuilder<T> deleteSqlBuilder;
    private boolean deleteInit;

    public CacheOptionalSqlBuilder(Class<T> entityClass) {
        this.selectSqlBuilder = new HandleSelectSqlBuilder<>(entityClass);
        this.insertSqlBuilder = new HandleInsertSqlBuilder<>();
        this.updateSqlBuilder = new HandleInsertSqlBuilder<>();
        this.deleteSqlBuilder = new HandleInsertSqlBuilder<>();

    }

    public AbstractSqlBuilder<T> getSelectSqlBuilder() {
        return selectSqlBuilder;
    }

    public boolean isSelectInit() {
        return selectInit;
    }

    public AbstractSqlBuilder<T> getInsertSqlBuilder() {
        return insertSqlBuilder;
    }

    public boolean isInsertInit() {
        return insertInit;
    }

    public AbstractSqlBuilder<T> getUpdateSqlBuilder() {
        return updateSqlBuilder;
    }

    public boolean isUpdateInit() {
        return updateInit;
    }

    public AbstractSqlBuilder<T> getDeleteSqlBuilder() {
        return deleteSqlBuilder;
    }

    public boolean isDeleteInit() {
        return deleteInit;
    }
}
