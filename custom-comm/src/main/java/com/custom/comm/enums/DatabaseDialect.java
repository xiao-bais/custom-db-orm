package com.custom.comm.enums;

/**
 * 数据库方言类型
 * @author  Xiao-Bai
 * @since 2022/10/27 17:51
 */
public enum DatabaseDialect {

    DEFAULT("默认", "MYSQL8.0+ 数据库", "com.mysql.cj.jdbc.Driver"),

    MYSQL8("MYSQL", "MYSQL8.0+ 数据库", "com.mysql.cj.jdbc.Driver"),

    MYSQL5("MYSQL", "MYSQL5.0+ 数据库", "com.mysql.jdbc.Driver"),

    ORACLE("ORACLE", "ORACLE数据库", "oracle.jdbc.driver.OracleDriver"),

    SQL_SERVER("SQL_SERVER", "SQL_SERVER 数据库", "com.mircosoft.sqlserver.jdbc.SQLServerDriver")


    ;


    private final String name;
    private final String desc;
    private final String driverClassName;

    DatabaseDialect(String name, String desc, String driverClassName) {
        this.name = name;
        this.desc = desc;
        this.driverClassName = driverClassName;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public static DatabaseDialect findTypeByDriver(String driverClassName) {
        DatabaseDialect[] databaseTypes = values();
        for (DatabaseDialect databaseType : databaseTypes) {
            if (databaseType.driverClassName.equals(driverClassName)) {
                return databaseType;
            }
        }
        throw new UnsupportedOperationException("暂不支持该数据库");
    }

}
