package com.custom.action.interfaces;

import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Xiao-Bai
 * @since 2023/3/8 19:04
 */
public interface ExecuteHandler {

    Logger logger = LoggerFactory.getLogger(ExecuteHandler.class);

    /**
     * 创建会话
     */
    <T> Object doExecute(JdbcSqlSessionFactory executorFactory, Class<T> target, Object[] params) throws Exception;

    /**
     * 获取映射类型
     */
    <T> Class<T> getMappedType(Object[] params);

    /**
     * 分类
     */
    MethodKind getKind();










}
