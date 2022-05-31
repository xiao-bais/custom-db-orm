package com.custom.springboot.tableinit;

/**
 * @author Xiao-Bai
 * @date 2022/5/29 11:27
 * @desc:创建表的字段
 */
public class ColumnCreateInfo {

    /**
     * 表字段
     */
    private String column;

    /**
     * 创建/新增表字段的sql
     */
    private String createColumnSql;


    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getCreateColumnSql() {
        return createColumnSql;
    }

    public void setCreateColumnSql(String createColumnSql) {
        this.createColumnSql = createColumnSql;
    }
}
