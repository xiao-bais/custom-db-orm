package com.custom.generator.config;

import com.custom.comm.enums.KeyStrategy;

/**
 * @Author Xiao-Bai
 * @Date 2022/4/19 11:17
 * @Desc：
 **/
public class GlobalConfig {

    /**
     * 生成的Java类的作者
     */
    private String author;

    /**
     * 生成的路径
     */
    private String outputDir;

    /**
     * 是否生成Swagger注解
     */
    private Boolean swagger = false;

    /**
     * 实体是否使用lombok注解
     */
    private Boolean entityLombok = false;

    private KeyStrategy keyStrategy;

    private String serviceName;

    private String serviceImplName;

    private String controllerName;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public Boolean getSwagger() {
        return swagger;
    }

    public void setSwagger(Boolean swagger) {
        this.swagger = swagger;
    }

    public KeyStrategy getKeyStrategy() {
        return keyStrategy;
    }

    public void setKeyStrategy(KeyStrategy keyStrategy) {
        this.keyStrategy = keyStrategy;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceImplName() {
        return serviceImplName;
    }

    public void setServiceImplName(String serviceImplName) {
        this.serviceImplName = serviceImplName;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public Boolean getEntityLombok() {
        return entityLombok;
    }

    public void setEntityLombok(Boolean entityLombok) {
        this.entityLombok = entityLombok;
    }
}
