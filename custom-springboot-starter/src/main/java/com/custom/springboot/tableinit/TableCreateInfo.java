package com.custom.springboot.tableinit;

import java.util.List;
import java.util.Set;

/**
 * @author Xiao-Bai
 * @date 2022/5/29 11:35
 * @desc:创建表对象
 */
public class TableCreateInfo {

    /**
     * 表名
     */
    private String table;

    /**
     * 表说明
     */
    private String comment;

    /**
     * 表主键的创建sql
     */
    private String primaryKeyCreateSql;

    /**
     * 表其他字段的创建sql
     */
    private Set<ColumnCreateInfo> columnCreateInfos;

    protected String getTable() {
        return table;
    }

    protected void setTable(String table) {
        this.table = table;
    }

    protected String getComment() {
        return comment;
    }

    protected void setComment(String comment) {
        this.comment = comment;
    }

    protected String getPrimaryKeyCreateSql() {
        return primaryKeyCreateSql;
    }

    protected void setPrimaryKeyCreateSql(String primaryKeyCreateSql) {
        this.primaryKeyCreateSql = primaryKeyCreateSql;
    }

    protected void mergeColumnCreateInfos(Set<ColumnCreateInfo> columnCreateInfos) {
        this.columnCreateInfos.addAll(columnCreateInfos);
    }

    protected Set<ColumnCreateInfo> getColumnCreateInfos() {
        return columnCreateInfos;
    }

    protected void setColumnCreateInfos(Set<ColumnCreateInfo> columnCreateInfos) {
        this.columnCreateInfos = columnCreateInfos;
    }
}
