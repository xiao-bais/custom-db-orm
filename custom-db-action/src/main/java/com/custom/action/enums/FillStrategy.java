package com.custom.action.enums;

/**
 * @author Xiao-Bai
 * @date 2021/11/2 13:24
 * @desc: 增删改时做字段填充
 *
 */
public enum FillStrategy {


    /**
    * 默认不做处理
    */
    DEFAULT,

    /**
    * 插入时填充字段指定值
    */
    INSERT,

    /**
    * 修改时更新字段指定值
    */
    UPDATE,

    /**
    * 插入或修改时填充指定字段值
    */
    INSERT_UPDATE

}
