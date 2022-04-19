package com.custom.action.generator.config;

/**
 * @Author Xiao-Bai
 * @Date 2022/4/19 11:23
 * @Desc：包的生成配置
 **/
public class PackageConfig {

    /**
     * 生成的模块归于哪个包下面
     */
    private String packageName;

    /**
     * 父类包，生成的包的父路径
     */
    private String parentPackage;

    /**
     * 生成的controller层的包名
     */
    private String controller;

    /**
     * 生成的controller层的包名
     */
    private String entity;

    /**
     * 生成的controller层的包名
     */
    private String service;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getParentPackage() {
        return parentPackage;
    }

    public void setParentPackage(String parentPackage) {
        this.parentPackage = parentPackage;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
