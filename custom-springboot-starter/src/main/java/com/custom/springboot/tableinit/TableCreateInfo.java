package com.custom.springboot.tableinit;

import java.util.List;

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

    private String primaryKeyCreateSql;

    private List<ColumnCreateInfo> columnCreateInfos;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPrimaryKeyCreateSql() {
        return primaryKeyCreateSql;
    }

    public void setPrimaryKeyCreateSql(String primaryKeyCreateSql) {
        this.primaryKeyCreateSql = primaryKeyCreateSql;
    }

    public List<ColumnCreateInfo> getColumnCreateInfos() {
        return columnCreateInfos;
    }

    public void setColumnCreateInfos(List<ColumnCreateInfo> columnCreateInfos) {
        this.columnCreateInfos = columnCreateInfos;
    }
}
