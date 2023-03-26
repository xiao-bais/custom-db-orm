package com.custom.tools.rbac;

import com.custom.tools.rbac.infs.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author Xiao-Bai
 * @since 2023/3/20 22:29
 */
public class CmRbacInfo<U, R, P> implements Serializable {

    private List<IUser<U>> userList;
    private List<IUserRole<U, R>> userRoleList;
    private List<IRole<R>> roleList;
    private List<IPermission<P>> permissionList;
    private List<IRolePermission<R, P>> rolePermissionList;

    public CmRbacInfo(List<IUser<U>> userList, List<IUserRole<U, R>> userRoleList, List<IRole<R>> roleList, List<IPermission<P>> permissionList, List<IRolePermission<R, P>> rolePermissionList) {
        this.userList = userList;
        this.userRoleList = userRoleList;
        this.roleList = roleList;
        this.permissionList = permissionList;
        this.rolePermissionList = rolePermissionList;
    }

    public CmRbacInfo() {

    }

    public List<IUser<U>> getUserList() {
        return userList;
    }

    public void setUserList(List<IUser<U>> userList) {
        this.userList = userList;
    }

    public List<IUserRole<U, R>> getUserRoleList() {
        return userRoleList;
    }

    public void setUserRoleList(List<IUserRole<U, R>> userRoleList) {
        this.userRoleList = userRoleList;
    }

    public List<IRole<R>> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<IRole<R>> roleList) {
        this.roleList = roleList;
    }

    public List<IPermission<P>> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<IPermission<P>> permissionList) {
        this.permissionList = permissionList;
    }

    public List<IRolePermission<R, P>> getRolePermissionList() {
        return rolePermissionList;
    }

    public void setRolePermissionList(List<IRolePermission<R, P>> rolePermissionList) {
        this.rolePermissionList = rolePermissionList;
    }
}
