package com.custom.tools.rbac;

/**
 * @author Xiao-Bai
 * @since 2023/3/21 11:57
 */
public interface CmRbacCache<U, R, P> {

    /**
     * 初始化
     */
    CmRbacInfo<U, R, P> initCache();

    /**
     * 获取缓存
     */
    CmRbacInfo<U, R, P> getCache();

    /**
     * 重置缓存
     */
    boolean resetCache();

}
