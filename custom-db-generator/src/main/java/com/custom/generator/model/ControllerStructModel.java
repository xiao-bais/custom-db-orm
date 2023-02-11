package com.custom.generator.model;

import java.util.List;

/**
 * 控制器模板构造对象
 * @author  Xiao-Bai
 * @since  2022/4/24 16:16
 **/
public class ControllerStructModel {

    /**
     * 类名
     */
    private String controllerName;

    /**
     * 父级包名(com.test.controller)
     */
    private String controllerPackage;

    /**
     * 父级包路径(com/test/controller)
     */
    private String controllerClassPath;

    /**
     * 来源包名(com.test.controller)
     */
    private String sourcePackage;

    /**
     * 作者
     */
    private String author;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 导入的包
     */
    private List<String> importPackages;

    /**
     * 请求路径API
     */
    private String requestPath;

    /**
     * 是否覆盖已存在的文件
     */
    private Boolean overrideEnable = true;



    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public String getControllerPackage() {
        return controllerPackage;
    }

    public void setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
    }

    public String getControllerClassPath() {
        return controllerClassPath;
    }

    public void setControllerClassPath(String controllerClassPath) {
        this.controllerClassPath = controllerClassPath;
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

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public Boolean getOverrideEnable() {
        return overrideEnable;
    }

    public void setOverrideEnable(Boolean overrideEnable) {
        this.overrideEnable = overrideEnable;
    }
}
