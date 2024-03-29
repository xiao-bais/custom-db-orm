package com.custom.comm.enums;

/**
 * 别名策略类型选择
 * @author  Xiao-Bai
 * @since 2022/8/30 0030 17:44
 */
public enum AliasStrategy {

    /**
     * 自定义表别名
     * <br/>由用户自行通过{@link LambdaJoinConditional#(String joinAlias)} 指定
     */
    INPUT,

    /**
     * 表名为各单词首字母拼接 (默认)
     *   <br/>例如: 表名 -> a_student_info 别名 -> asi
     *   <br/>例如: 表名 -> aStudentInfo 别名 -> asi
     *   <br/>例如: 表名 -> astudentinfo 别名 -> a
     */
    FIRST_APPEND,

    /**
     * 系统生成唯一ID
     */
    UNIQUE_ID


}
