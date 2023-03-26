package com.custom.tools.rbac.infs;

import java.io.Serializable;

/**
 * 角色-权限 关联
 * @author Xiao-Bai
 * @since 2023/3/20 22:26
 */
public interface IRolePermission<R, P> extends Serializable {

    /**
     * 获取角色ID
     */
    R getCmRoleId();

    /**
     * 获取权限ID
     */
    P getCmPermissionId();


    default boolean testRole(IRole<R> role) {
        return role != null && role.getCmRoleId().equals(getCmRoleId());
    }

    default boolean testPermission(P permissionId) {
        return permissionId != null && permissionId.equals(getCmPermissionId());
    }

}
