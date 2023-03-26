package com.custom.taskmanager.config;

import com.custom.action.core.JdbcDao;
import com.custom.taskmanager.entity.RbacPermission;
import com.custom.taskmanager.entity.RbacRole;
import com.custom.taskmanager.entity.RbacRolePermission;
import com.custom.taskmanager.entity.RbacUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author Xiao-Bai
 * @since 2023/3/21 12:25
 */
@Component
public class SystemInit implements ApplicationRunner {

    @Autowired
    private JdbcDao jdbcDao;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        jdbcDao.createTables(
                RbacRole.class,
                RbacUserRole.class,
                RbacPermission.class,
                RbacRolePermission.class
        );
    }
}
