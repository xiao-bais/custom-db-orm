package com.custom.taskmanager.entity;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbTable;
import com.custom.tools.rbac.infs.IPermission;
import lombok.Data;

/**
 * @author Xiao-Bai
 * @since 2023/3/21 12:20
 */
@Data
@DbTable("rbac_permission")
public class RbacPermission implements IPermission<String> {

    @DbKey
    private String id;

    @DbField
    private String name;


    @Override
    public String getCmPermissionId() {
        return id;
    }
}
