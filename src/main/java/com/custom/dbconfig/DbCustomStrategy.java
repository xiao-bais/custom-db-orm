package com.custom.dbconfig;

/**
 * @Author Xiao-Bai
 * @Date 2021/8/22
 * @Description
 */
public class DbCustomStrategy {

    /**
     * 下划线转驼峰
     */
    private boolean underlineToCamel = true;

    /**
     * 驼峰转下划线
     */
    private boolean camelToUnderline = false;

    /**
     * 打印sql
     */
    private boolean printSqlFlag = false;

    public boolean isPrintSqlFlag() {
        return printSqlFlag;
    }

    public void setPrintSqlFlag(boolean printSqlFlag) {
        this.printSqlFlag = printSqlFlag;
    }

    public boolean isUnderlineToCamel() {
        return underlineToCamel;
    }

    public void setUnderlineToCamel(boolean underlineToCamel) {
        this.underlineToCamel = underlineToCamel;
    }

    public boolean isCamelToUnderline() {
        return camelToUnderline;
    }

    public void setCamelToUnderline(boolean camelToUnderline) {
        this.camelToUnderline = camelToUnderline;
    }
}
