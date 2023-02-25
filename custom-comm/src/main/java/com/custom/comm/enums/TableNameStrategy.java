package com.custom.comm.enums;

/**
 * @author Xiao-Bai
 * @since 2023/2/25 14:21
 */
public enum TableNameStrategy {

    /**
     * 不做处理，直接拼接前缀(该方式不受驼峰转下划线策略影响)
     * <br/> 例如 tablePrefix=tab,entity=Employee
     * <br/> 那么 tableName=tabEmployee
     */
    APPEND,

    /**
     * 全部转为小写(该方式不受驼峰转下划线策略影响)
     * <br/> 例如 tablePrefix=tab(Tab),entity=Employee
     * <br/> 那么 tableName=tabemployee
     */
    LOWERCASE,

    /**
     * 拼接前缀(受驼峰转下划线策略影响)
     * <br/> 例如 tablePrefix=tab,entity=Employee
     * <br/> 若驼峰转下划线=true 则 tableName=tab_employee
     * <br/> 若驼峰转下划线=false 则等同于{@link #APPEND}
     */
    DEFAULT


}
