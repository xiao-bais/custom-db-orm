package com.custom.action.generator.config;

/**
 * @Author Xiao-Bai
 * @Date 2022/4/19 11:38
 * @Desc：
 **/
public class TableConfig {

    /**
     * 生成时，若配置了表前缀，则生成后前缀会忽略
     * 例：tb_user -> User
     */
    private String tablePrefix;

    /**
     * 生成事，若配置了实体的后缀，则生成后自动添加后缀
     * 例：entitySuffix: PO,则 tb_user -> UserPO
     */
    private String entitySuffix;

    /**
     * 配置公共父类实体Class对象
     * 例：BaseModel.class
     */
    private Class<?> parentEntityClass;

    /**
     * 生成@DbField注解时，是否加上对应表字段名称
     * false : @DbField, true : @DbField(value = "name")
     */
    private Boolean entityDbFieldAnnotationValueEnable = false;


    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public String getEntitySuffix() {
        return entitySuffix;
    }

    public void setEntitySuffix(String entitySuffix) {
        this.entitySuffix = entitySuffix;
    }

    public Class<?> getParentEntityClass() {
        return parentEntityClass;
    }

    public void setParentEntityClass(Class<?> parentEntityClass) {
        this.parentEntityClass = parentEntityClass;
    }

    public Boolean getEntityDbFieldAnnotationValueEnable() {
        return entityDbFieldAnnotationValueEnable;
    }

    public void setEntityDbFieldAnnotationValueEnable(Boolean entityDbFieldAnnotationValueEnable) {
        this.entityDbFieldAnnotationValueEnable = entityDbFieldAnnotationValueEnable;
    }
}
