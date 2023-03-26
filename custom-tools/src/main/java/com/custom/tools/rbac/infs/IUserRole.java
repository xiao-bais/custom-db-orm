package com.custom.tools.rbac.infs;

import java.io.Serializable;

/**
 * 用户-角色 关联
 * @author Xiao-Bai
 * @since 2023/3/20 22:17
 */
public interface IUserRole<U, R> extends Serializable {

    /**
     * 获取用户ID
     */
    U getCmUserId();

    /**
     * 获取角色ID
     */
    R getCmRoleId();


    default boolean testUser(IUser<U> user) {
        return user != null && user.getCmUserId().equals(getCmUserId());
    }

    default boolean testRole(R roleId) {
        return roleId != null && roleId.equals(getCmRoleId());
    }

}
