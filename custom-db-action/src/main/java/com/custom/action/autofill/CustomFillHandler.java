package com.custom.action.autofill;

import java.util.List;

/**
 * 实现该接口，并加入spring容器中即可实现自动填充功能
 * @author Xiao-Bai
 * @since 2023/2/23 12:50
 */
public interface CustomFillHandler {

    /**
     * 处理自动填充
     * <br/> 注意：该填充为全局填充，也就是说对所有的表都有效，若有两个或以上的不同填充方式，请选择{@link #handleMany(List)}
     * <br/> 该方法中，target不生效
     */
    void handle(CustomTableFill fill);

    /**
     * 处理自动填充
     * <br/> 当不同的表有不同的填充方式时，可选择该方法
     * <br/> 注意: 添加的多个填充对象中，只能存在一个主填充对象
     * 若{@link #handle(CustomTableFill)} 填写了填充配置，则默认为主填充对象，此时{@link #handleMany(List)} 中不允许出现其他主填充对象
     * <br/> 主填充对象可作用在所有未指定填充的表
     */
    void handleMany(List<CustomTableFill> fillList);

}
