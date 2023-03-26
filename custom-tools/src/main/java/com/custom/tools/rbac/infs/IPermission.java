package com.custom.tools.rbac.infs;

import java.io.Serializable;

/**
 * 权限
 * @author Xiao-Bai
 * @since 2023/3/20 22:06
 */
public interface IPermission<P> extends Serializable {

    /**
     * 获取权限ID
     */
    P getCmPermissionId();

}
