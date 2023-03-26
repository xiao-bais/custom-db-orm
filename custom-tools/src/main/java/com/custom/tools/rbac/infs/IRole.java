package com.custom.tools.rbac.infs;

import java.io.Serializable;

/**
 * 角色
 * @author Xiao-Bai
 * @since 2023/3/20 22:10
 */
public interface IRole<R> extends Serializable {

    /**
     * 获取角色ID
     */
    R getCmRoleId();
}
