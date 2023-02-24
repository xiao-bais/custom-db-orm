package com.custom.action.autofill;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xiao-Bai
 * @since 2023/2/22 23:08
 */
public class CustomTableFill {

    /**
     * target仅仅作用于单表填充配置时的实体类
     * <br/> 指定填充的目标实体类，若globalFill=true，那么target无效
     */
    private Class<?> target;

    /**
     * 是否全局填充，默认全局使用
     * <br/> 当globalFill=true时，target无效，该配置对所有表生效
     */
    private boolean globalFill = true;

    /**
     * 多种填充方式集合
     */
    private final List<FillObject> fillObjects = new ArrayList<>();


    private CustomTableFill() {

    }


    public static CustomTableFill builder() {
        return new CustomTableFill();
    }

    /**
     * 填充的目标类
     */
    public CustomTableFill target(Class<?> target) {
        this.target = target;
        this.globalFill = false;
        return this;
    }

    /**
     * 开启全局填充
     */
    public CustomTableFill globalFill() {
        this.globalFill = true;
        this.target = null;
        return this;
    }

    /**
     * 添加填充值
     * @param obj 填充值
     * 例如: FillObject.instance("createTime", Long.class, System.currentTimeMillis() / 1000L, FillStrategy.INSERT)
     * 例如: FillObject.instance("updateTime", Long.class, () -> System.currentTimeMillis() / 1000L, FillStrategy.INSERT_UPDATE)
     */
    public CustomTableFill addFill(FillObject obj) {
        this.fillObjects.add(obj);
        return this;
    }

    public Class<?> getTarget() {
        return target;
    }

    public boolean isGlobalFill() {
        return globalFill;
    }

    public List<FillObject> getFillObjects() {
        return fillObjects;
    }

    public boolean isFillEmpty() {
        return fillObjects.isEmpty();
    }
}
