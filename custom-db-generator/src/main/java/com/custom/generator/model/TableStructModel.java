package com.custom.generator.model;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/4/19 14:48
 * @Desc：表结构解析模板
 **/
public class TableStructModel {

    /**
     * 表名
     */
    private String table;

    /**
     * 实体名称
     */
    private String entityName;

    /**
     * 实体里包名
     */
    private String entityPackage;

    /**
     * 实体生成全路径
     * path：com/example/test/Student.java
     */
    private String entityClassPath;

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

    /**
     * 包的来源
     * package com.example.test
     */
    private String sourcePackage;
    /**
     * 导入包信息
     * java：java自带的类
     * other：除java外的其他类
     */
    private List<String> importJavaPackages;
    private List<String> importOtherPackages;

    /**
     * 是否覆盖已存在的文件
     */
    private Boolean overrideEnable = true;

    /**
     * 作者
     */
    private String author;


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

    public String getSourcePackage() {
        return sourcePackage;
    }

    public void setSourcePackage(String sourcePackage) {
        this.sourcePackage = sourcePackage;
    }

    public List<String> getImportJavaPackages() {
        return importJavaPackages;
    }

    public void setImportJavaPackages(List<String> importJavaPackages) {
        this.importJavaPackages = importJavaPackages;
    }

    public List<String> getImportOtherPackages() {
        return importOtherPackages;
    }

    public void setImportOtherPackages(List<String> importOtherPackages) {
        this.importOtherPackages = importOtherPackages;
    }

    public String getEntityClassPath() {
        return entityClassPath;
    }

    public void setEntityClassPath(String entityClassPath) {
        this.entityClassPath = entityClassPath;
    }

    public String getEntityPackage() {
        return entityPackage;
    }

    public void setEntityPackage(String entityPackage) {
        this.entityPackage = entityPackage;
    }

    public Boolean getOverrideEnable() {
        return overrideEnable;
    }

    public void setOverrideEnable(Boolean overrideEnable) {
        this.overrideEnable = overrideEnable;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
