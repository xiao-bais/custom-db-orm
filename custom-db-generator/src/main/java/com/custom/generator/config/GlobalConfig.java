package com.custom.generator.config;

import com.custom.comm.enums.KeyStrategy;

import java.util.Objects;

/**
 * 全局配置
 * @author  Xiao-Bai
 * @since  2022/4/19 11:17
 **/
public class GlobalConfig {

    /**
     * 生成的Java类的作者
     */
    private String author = System.getProperty("user.name");

    /**
     * 生成的路径
     */
    private String outputDir = "src/main/java";

    /**
     * 是否生成Swagger注解
     */
    private Boolean swagger = false;

    /**
     * 实体是否使用lombok注解
     */
    private Boolean entityLombok = false;

    /**
     * 主键策略
     */
    private KeyStrategy keyStrategy;

    /**
     * service接口名称
     */
    private String serviceName = "%sService";

    /**
     * service接口实现类名称
     */
    private String serviceImplName = "%sServiceImpl";

    /**
     * 控制器名称
     */
    private String controllerName = "%sController";

    /**
     * 是否覆盖已存在的文件
     */
    private Boolean overrideEnable = true;

    public String getAuthor() {
        return Objects.isNull(author) ? "" : author;
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

    public Boolean getOverrideEnable() {
        return overrideEnable;
    }

    public void setOverrideEnable(Boolean overrideEnable) {
        this.overrideEnable = overrideEnable;
    }
}
