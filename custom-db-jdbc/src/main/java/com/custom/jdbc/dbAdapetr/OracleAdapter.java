package com.custom.jdbc.dbAdapetr;

import com.custom.comm.enums.DatabaseDialect;
import com.custom.comm.utils.AssertUtil;
import com.custom.jdbc.session.JdbcSqlSessionFactory;

/**
 * @author  Xiao-Bai
 * @since  2022/10/27 18:52
 * 
 */
public class OracleAdapter extends AbstractDbAdapter {



    @Override
    public String databaseName() {
        String url = getExecutorFactory().getDbDataSource().getUrl();
        return url.substring(url.lastIndexOf(":"));
    }

    @Override
    public String driverClassName() {
        return DatabaseDialect.ORACLE.getDriverClassName();
    }

    @Override
    public DatabaseDialect getType() {
        return DatabaseDialect.ORACLE;
    }

    @Override
    public String hostName() {
        return null;
    }

    @Override
    public String handlePage(String originSql, long pageIndex, long pageSize) {
        return new StringBuilder()
                .append("SELECT tab_x_page.* FROM ( SELECT tab_x.*, rownum rm FROM (\n ")
                .append(originSql)
                .append(" \n\t) tab_x WHERE rownum <= ")
                .append(pageIndex * pageSize)
                .append(" ) tab_x_page WHERE tab_x_page.rm > ")
                .append((pageIndex - 1) * pageSize)
                .toString();
    }

    @Override
    public boolean existTable(String table) {
        AssertUtil.npe(table);
        String existSql = String.format("SELECT count(*) AS count FROM USER_OBJECTS WHERE OBJECT_NAME = '%s'", table);
        return queryBoolean(existSql);
    }

    @Override
    public boolean existColumn(String table, String column) {
        AssertUtil.npe(table);
        AssertUtil.npe(column);
        String existSql = String.format("SELECT COUNT(*) AS count FROM USER_TAB_COLUMNS WHERE TABLE_NAME = '%s' AND COLUMN_NAME = '%s'", table, column);
        return queryBoolean(existSql);
    }

    public OracleAdapter(JdbcSqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }
}
