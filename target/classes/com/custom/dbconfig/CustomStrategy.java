package com.custom.dbconfig;

/**
 * @Author Xiao-Bai
 * @Date 2021/8/22
 * @Description
 */
public class CustomStrategy {

    /**
     * 下划线转驼峰
     */
    private boolean underlineToCamel = true;

    /**
     * 驼峰转下划线
     */
    private boolean camelToUnderline = false;


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
