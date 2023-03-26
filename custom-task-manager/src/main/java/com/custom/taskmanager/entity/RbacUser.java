package com.custom.taskmanager.entity;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbTable;
import com.custom.tools.rbac.infs.IUser;
import lombok.Data;

/**
 * @author Xiao-Bai
 * @since 2023/3/21 12:18
 */
@Data
@DbTable("rbac_user")
public class RbacUser implements IUser<String> {

    @DbKey
    private String id;

    @DbField
    private String name;

    @DbField
    private String phone;

    @DbField
    private Boolean superUser;

    @Override
    public String getCmUserId() {
        return id;
    }

    @Override
    public boolean isSuperCmUser() {
        return superUser != null && superUser;
    }
}
