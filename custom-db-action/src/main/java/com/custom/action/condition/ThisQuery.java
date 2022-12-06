package com.custom.action.condition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xiao-Bai
 * @date 2022/12/6 0006 16:22
 */
public class ThisQuery {

    /**
     * 查询的sql
     */
    private String selectSql;

    /**
     * sql参数
     */
    private List<Object> params = new ArrayList<>();

    public ThisQuery() {
    }

    public String getSelectSql() {
        return selectSql;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setSelectSql(String selectSql) {
        this.selectSql = selectSql;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "ThisQuery{" +
                "selectSql='" + selectSql + '\'' +
                ", params=" + params +
                '}';
    }
}
