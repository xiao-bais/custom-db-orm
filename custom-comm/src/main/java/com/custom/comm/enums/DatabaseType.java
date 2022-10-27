package com.custom.comm.enums;

/**
 * @author Xiao-Bai
 * @date 2022/10/27 17:51
 * 数据库类型
 */
public enum DatabaseType {

    DEFAULT("默认", "MYSQL8.0+ 数据库", "com.mysql.cj.jdbc.Driver"),

    MYSQL8("MYSQL", "MYSQL8.0+ 数据库", "com.mysql.cj.jdbc.Driver"),

    MYSQL5("MYSQL", "MYSQL5.0+ 数据库", "com.mysql.jdbc.Driver"),

    ORACLE("ORACLE", "ORACLE数据库", "oracle.jdbc.driver.OracleDriver"),

    SQL_SERVER("SQL_SERVER", "SQL_SERVER 数据库", "com.mircosoft.sqlserver.jdbc.SQLServerDriver")


    ;


    private final String name;
    private final String desc;
    private final String driverClassName;

    DatabaseType(String name, String desc, String driverClassName) {
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

    public static DatabaseType findTypeByDriver(String driverClassName) {
        DatabaseType[] databaseTypes = values();
        for (DatabaseType databaseType : databaseTypes) {
            if (databaseType.driverClassName.equals(driverClassName)) {
                return databaseType;
            }
        }
        throw new UnsupportedOperationException("暂不支持该数据库");
    }

}
