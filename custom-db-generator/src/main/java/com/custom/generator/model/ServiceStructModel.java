package com.custom.generator.model;


import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/4/21 10:55
 * @Desc：service接口层模板构造对象
 **/
public class ServiceStructModel {

    /**
     * 类名
     */
    private String className;

    /**
     * 父级包名
     */
    private String servicePackage;

    /**
     * 本类来源包
     */
    private String sourcePackage;

    /**
     * 作者
     */
    private String author;

    /**
     * 创建日期
     */
    private String createDate;

    /**
     * 导入的包
     */
    private List<String> importPackages;

    /**
     * 是否覆盖已存在的文件
     */
    private Boolean overrideEnable = true;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSourcePackage() {
        return sourcePackage;
    }

    public void setSourcePackage(String sourcePackage) {
        this.sourcePackage = sourcePackage;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public List<String> getImportPackages() {
        return importPackages;
    }

    public void setImportPackages(List<String> importPackages) {
        this.importPackages = importPackages;
    }

    public String getServicePackage() {
        return servicePackage;
    }

    public void setServicePackage(String servicePackage) {
        this.servicePackage = servicePackage;
    }

    public Boolean getOverrideEnable() {
        return overrideEnable;
    }

    public void setOverrideEnable(Boolean overrideEnable) {
        this.overrideEnable = overrideEnable;
    }
}
