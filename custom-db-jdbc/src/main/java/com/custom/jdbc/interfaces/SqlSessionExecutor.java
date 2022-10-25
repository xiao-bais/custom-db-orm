package com.custom.jdbc.interfaces;

import com.custom.jdbc.session.CustomSqlSession;

import java.sql.Connection;

/**
 * @author Xiao-Bai
 * @date 2022/10/23 18:37
 * @desc
 */
public interface SqlSessionExecutor {

    CustomSqlSession createSession(Connection conn);

}
