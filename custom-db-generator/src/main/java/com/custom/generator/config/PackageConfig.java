package com.custom.generator.config;

import java.util.Objects;

/**
 * @author  Xiao-Bai
 * @since  2022/4/19 11:23
 * @Desc：包的生成配置
 **/
public class PackageConfig {

    /**
     * 生成的模块归于哪个包下面
     */
    private String parentPackage;

    /**
     * 生成的controller层的包名
     */
    private String controller = "controller";

    /**
     * 生成的controller层的包名
     */
    private String entity = "entity";

    /**
     * 生成的controller层的包名
     */
    private String service = "service";

    public String getParentPackage() {
        return parentPackage;
    }

    public void setParentPackage(String parentPackage) {
        this.parentPackage = parentPackage;
    }

    public String getController() {
        return Objects.isNull(controller) ? "" : controller.trim();
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getEntity() {
        return Objects.isNull(entity) ? "" : entity.trim();
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getService() {
        return Objects.isNull(service) ? "" : service.trim();
    }

    public void setService(String service) {
        this.service = service;
    }
}
