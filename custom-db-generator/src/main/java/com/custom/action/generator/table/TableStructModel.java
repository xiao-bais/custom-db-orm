package com.custom.action.generator.table;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/4/19 14:48
 * @Desc：表结构解析模板
 **/
public class TableStructModel<T> {

    /**
     * 表名
     */
    private String table;

    /**
     * 实体名称
     */
    private String entityName;

    /**
     * 实体生成全路径
     */
    private String entityClassName;

    /**
     * 表说明
     */
    private String desc;

    /**
     * 表字段
     */
    private List<ColumnStructModel> columnStructModels;

    /**
     * 是否生成lombok
     */
    private Boolean lombok = false;

    /**
     * 是否生成swagger注解
     */
    private Boolean swagger = false;




    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<ColumnStructModel> getColumnStructModels() {
        return columnStructModels;
    }

    public void setColumnStructModels(List<ColumnStructModel> columnStructModels) {
        this.columnStructModels = columnStructModels;
    }

    public Boolean getLombok() {
        return lombok;
    }

    public void setLombok(Boolean lombok) {
        this.lombok = lombok;
    }

    public Boolean getSwagger() {
        return swagger;
    }

    public void setSwagger(Boolean swagger) {
        this.swagger = swagger;
    }
}
