package com.custom.taskmanager.entity;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbTable;
import com.custom.tools.rbac.infs.IUserRole;
import lombok.Data;

/**
 * @author Xiao-Bai
 * @since 2023/3/21 12:22
 */
@Data
@DbTable("rbac_user_role")
public class RbacUserRole implements IUserRole<String, String> {

    @DbKey
    private Integer id;

    @DbField
    private String userId;

    @DbField
    private String roleId;


    @Override
    public String getCmUserId() {
        return userId;
    }

    @Override
    public String getCmRoleId() {
        return roleId;
    }
}
