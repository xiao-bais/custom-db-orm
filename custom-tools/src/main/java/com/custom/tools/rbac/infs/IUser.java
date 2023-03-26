package com.custom.tools.rbac.infs;

import java.io.Serializable;

/**
 * 用户
 * @author Xiao-Bai
 * @since 2023/3/20 22:16
 */
public interface IUser<U> extends Serializable {

    /**
     * 获取用户ID
     */
    U getCmUserId();

    /**
     * 是否是超级用户
     */
    boolean isSuperCmUser();
}
