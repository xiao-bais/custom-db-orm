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

    @Override
    public int hashCode() {
        int initCode = 17;
        int result = 31 * initCode + (column == null ? 0 : column.hashCode());
        result = 31 * result + (column == null ? 0 : column.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof ColumnCreateInfo) {
            ColumnCreateInfo other = (ColumnCreateInfo) obj;
            return this.column.equals(other.column) && this.createColumnSql.equals(other.createColumnSql);
        }
        return false;
    }
}
