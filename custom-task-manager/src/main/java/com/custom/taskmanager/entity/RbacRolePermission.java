package com.custom.taskmanager.entity;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbTable;
import com.custom.tools.rbac.infs.IRolePermission;
import lombok.Data;

/**
 * @author Xiao-Bai
 * @since 2023/3/21 12:23
 */
@Data
@DbTable("rbac_role_permission")
public class RbacRolePermission implements IRolePermission<String, String> {

    @DbKey
    private Integer id;

    @DbField
    private String roleId;

    @DbField
    private String permissionId;

    @Override
    public String getCmRoleId() {
        return roleId;
    }

    @Override
    public String getCmPermissionId() {
        return permissionId;
    }
}
