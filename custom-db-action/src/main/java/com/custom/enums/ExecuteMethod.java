package com.custom.enums;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/17 10:20
 * @Desc：定义方法执行的种类：增，删，改，查
 **/
public enum ExecuteMethod {

    /**
    * 插入方法
    */
    INSERT,

    /**
    * 删除方法
    */
    DELETE,

    /**
    * 修改方法
    */
    UPDATE,

    /**
    * 查询方法
    */
    SELECT,

    /**
    * 默认不做处理
    */
    NONE

}
