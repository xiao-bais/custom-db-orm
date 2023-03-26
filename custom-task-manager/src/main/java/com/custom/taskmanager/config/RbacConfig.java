package com.custom.taskmanager.config;

import com.alibaba.fastjson2.JSONObject;
import com.custom.action.core.JdbcDao;
import com.custom.comm.utils.StrUtils;
import com.custom.taskmanager.entity.*;
import com.custom.tools.rbac.CmRbacCache;
import com.custom.tools.rbac.CmRbacHelper;
import com.custom.tools.rbac.CmRbacInfo;
import com.custom.tools.rbac.infs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Xiao-Bai
 * @since 2023/3/21 13:00
 */
@Configuration
@SuppressWarnings("unchecked")
public class RbacConfig {

    @Autowired
    private JdbcDao jdbcDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String RBAC_KEY = "RBAC_KEY";

    @Bean
    public <U, R, P> CmRbacHelper<U, R, P> getHelper() {
        return new CmRbacHelper<>(new CmRbacCache<U, R, P>() {

            @Override
            public CmRbacInfo<U, R, P> initCache() {
                System.out.println("rbac init....");
                CmRbacInfo<U, R, P> cmRbacInfo = new CmRbacInfo<>();

                // 设置用户
                List<RbacUser> rbacUsers = jdbcDao.selectList(RbacUser.class, "");
                cmRbacInfo.setUserList(rbacUsers.stream().map(x -> (IUser<U>) x).collect(Collectors.toList()));

                // 设置角色
                List<RbacRole> rbacRoles = jdbcDao.selectList(RbacRole.class, "");
                cmRbacInfo.setRoleList(rbacRoles.stream().map(x -> (IRole<R>) x).collect(Collectors.toList()));

                // 设置用户角色关联
                List<RbacUserRole> rbacUserRoles = jdbcDao.selectList(RbacUserRole.class, "");
                cmRbacInfo.setUserRoleList(rbacUserRoles.stream().map(x -> (IUserRole<U, R>) x).collect(Collectors.toList()));

                // 设置权限
                List<RbacPermission> rbacPermissions = jdbcDao.selectList(RbacPermission.class, "");
                cmRbacInfo.setPermissionList(rbacPermissions.stream().map(x -> (IPermission<P>) x).collect(Collectors.toList()));

                // 设置角色权限关联
                List<RbacRolePermission> rbacRolePermissions = jdbcDao.selectList(RbacRolePermission.class, "");
                cmRbacInfo.setRolePermissionList(rbacRolePermissions.stream().map(x -> (IRolePermission<R, P>) x).collect(Collectors.toList()));

                return cmRbacInfo;
            }

            @Override
            public CmRbacInfo<U, R, P> getCache() {
                Object o = redisTemplate.opsForValue().get(RBAC_KEY);
                String rbacString = String.valueOf(o);
                CmRbacInfo<U, R, P> cmRbacInfo;
                if (StrUtils.isNotBlank(rbacString)) {
                    cmRbacInfo = JSONObject.parseObject(rbacString, CmRbacInfo.class);
                } else {
                    cmRbacInfo = this.initCache();
                }
                return cmRbacInfo;
            }

            @Override
            public boolean resetCache() {
                this.initCache();
                return true;
            }
        });
    }



}
