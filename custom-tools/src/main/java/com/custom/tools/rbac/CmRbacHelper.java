package com.custom.tools.rbac;

import com.custom.tools.rbac.infs.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Xiao-Bai
 * @since 2023/3/20 22:03
 */
public class CmRbacHelper<U, R, P> {

    private final CmRbacCache<U, R, P> cmRbacCache;

    public CmRbacHelper(CmRbacCache<U, R, P> cmRbacCache) {
        this.cmRbacCache = cmRbacCache;
    }

    /**
     * 获取所有RBAC信息
     */
    public CmRbacInfo<U, R, P> getRbacAll() {
        CmRbacInfo<U, R, P> cache = cmRbacCache.getCache();
        if (cache == null) {
            cache = cmRbacCache.initCache();
        }
        return cache;
    }

    /**
     * 重置RBAC缓存
     */
    public boolean resetRbac() {
        return cmRbacCache.resetCache();
    }

    /**
     * 判断用户是否拥有该角色
     * @param user 用户
     * @param roleId 角色ID
     */
    public boolean userHasRole(IUser<U> user, R roleId) {
        if (user == null || roleId == null) {
            return false;
        }
        List<IUserRole<U, R>> userRoles = getUserRoleList(user);
        if (userRoles == null) return false;
        return userRoles.stream().anyMatch(x -> x.testRole(roleId));
    }

    /**
     * 判断用户是否拥有该角色
     * @param user 用户
     * @param role 角色
     */
    public boolean userHasRole(IUser<U> user, IRole<R> role) {
        if (user == null || role == null) {
            return false;
        }
        return userHasRole(user, role.getCmRoleId());
    }


    /**
     * 判断用户是否拥有该角色
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    public boolean userHasRole(U userId, R roleId) {
        if (userId == null || roleId == null) {
            return false;
        }
        CmRbacInfo<U, R, P> cmRbacInfo = getRbacAll();
        if (cmRbacInfo == null || cmRbacInfo.getUserList() == null) {
            return false;
        }
        Optional<IUser<U>> first = cmRbacInfo.getUserList().stream().filter(x -> x.getCmUserId().equals(userId)).findFirst();
        if (first.isPresent()) {
            IUser<U> user = first.get();
            if (user.isSuperCmUser()) {
                return true;
            }
            return userHasRole(user, roleId);
        }
        return false;
    }

    /**
     * 判断用户是否拥有该角色
     * @param userId 用户ID
     * @param role 角色
     */
    public boolean userHasRole(U userId, IRole<R> role) {
        if (userId == null || role == null) {
            return false;
        }
        return userHasRole(userId, role.getCmRoleId());
    }

    /**
     * 判断用户是否拥有该权限
     * @param user 用户
     * @param permission 权限
     */
    public boolean userHasPermission(IUser<U> user, IPermission<P> permission) {
        if (user == null || permission == null) {
            return false;
        }
        if (user.isSuperCmUser()) {
            return true;
        }
        return userHasPermission(user, permission.getCmPermissionId());
    }

    /**
     * 判断用户是否拥有该权限
     * @param user 用户
     * @param permissionId 权限ID
     */
    public boolean userHasPermission(IUser<U> user, P permissionId) {
        if (user == null || permissionId == null) {
            return false;
        }
        if (user.isSuperCmUser()) {
            return true;
        }
        List<IRolePermission<R, P>> rolePermissions = getPermissionListByUser(user);
        return rolePermissions != null && rolePermissions.stream().anyMatch(x -> x.testPermission(permissionId));
    }


    /**
     * 判断角色是否拥有该权限
     * @param role 角色
     * @param permission 权限
     */
    public boolean roleHasPermission(IRole<R> role, IPermission<P> permission) {
        if (role == null || permission == null) {
            return false;
        }
        return roleHasPermission(role, permission.getCmPermissionId());
    }

    /**
     * 判断角色是否拥有该权限
     * @param role 角色
     * @param permissionId 权限ID
     */
    public boolean roleHasPermission(IRole<R> role, P permissionId) {
        if (role == null || permissionId == null) {
            return false;
        }
        List<IRolePermission<R, P>> rolePermissions = getRolePermissionList(role);
        if (rolePermissions == null) return false;
        return rolePermissions.stream().anyMatch(x -> x.testPermission(permissionId));
    }

    /**
     * 判断角色是否拥有该权限
     * @param roleId 角色ID
     * @param permission 权限
     */
    public boolean roleHasPermission(R roleId, IPermission<P> permission) {
        if (roleId == null || permission == null) {
            return false;
        }
        return roleHasPermission(roleId, permission.getCmPermissionId());
    }

    /**
     * 判断角色是否拥有该权限
     * @param roleId 角色ID
     * @param permissionId 权限ID
     */
    public boolean roleHasPermission(R roleId, P permissionId) {
        if (roleId == null || permissionId == null) {
            return false;
        }
        CmRbacInfo<U, R, P> cmRbacInfo = getRbacAll();
        if (cmRbacInfo == null || cmRbacInfo.getRoleList() == null) {
            return false;
        }
        Optional<IRole<R>> first = cmRbacInfo.getRoleList().stream().filter(x -> x.getCmRoleId().equals(roleId)).findFirst();
        if (first.isPresent()) {
            IRole<R> role = first.get();
            return roleHasPermission(role, permissionId);
        }
        return false;
    }


    private List<IRolePermission<R, P>> getPermissionListByUser(IUser<U> user) {
        CmRbacInfo<U, R, P> cmRbacInfo = getRbacAll();
        if (cmRbacInfo == null) {
            return null;
        }
        List<IUserRole<U, R>> userRoles = cmRbacInfo.getUserRoleList().stream().filter(x -> x.testUser(user)).collect(Collectors.toList());
        if (userRoles.isEmpty()) {
            return null;
        }
        List<R> roleIds = userRoles.stream().map(IUserRole::getCmRoleId).collect(Collectors.toList());
        return cmRbacInfo.getRolePermissionList()
                .stream()
                .filter(x -> roleIds.contains(x.getCmRoleId())).
                collect(Collectors.toList());
    }


    private List<IRolePermission<R, P>> getRolePermissionList(IRole<R> role) {
        CmRbacInfo<U, R, P> cmRbacInfo = getRbacAll();
        if (cmRbacInfo == null || cmRbacInfo.getRolePermissionList() == null) {
            return null;
        }
        return cmRbacInfo.getRolePermissionList().stream().filter(x -> x.testRole(role)).collect(Collectors.toList());
    }


    private List<IUserRole<U, R>> getUserRoleList(IUser<U> user) {
        CmRbacInfo<U, R, P> cmRbacInfo = getRbacAll();
        if (cmRbacInfo == null || cmRbacInfo.getUserRoleList() == null) {
            return null;
        }
        return cmRbacInfo.getUserRoleList().stream().filter(x -> x.testUser(user)).collect(Collectors.toList());
    }

}
