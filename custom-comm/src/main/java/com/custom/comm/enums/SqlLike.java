package com.custom.comm.enums;

import com.custom.comm.utils.Constants;

/**
 * @Author Xiao-Bai
 * @Date 2022/2/16 18:05
 * @Desc：模糊查询枚举
 **/
public enum SqlLike {

    /**
    * 普通模糊查询
    */
    LIKE,

    /**
    * 向左模糊查询
    */
    LEFT,

    /**
     * 向右模糊查询
     */
    RIGHT;

    /**
     * 拼接模糊查询
     */
    public static String sqlLikeConcat(SqlLike sqlLike) {
        String sql = Constants.EMPTY;
        switch (sqlLike) {
            case LEFT:
                sql = "CONCAT('%', ?)";
                break;
            case RIGHT:
                sql = "CONCAT(?, '%')";
                break;
            case LIKE:
                sql = "CONCAT('%', ?, '%')";
                break;
        }
        return sql;
    }

}
