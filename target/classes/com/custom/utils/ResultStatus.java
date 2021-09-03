package com.custom.utils;

/**
 * @Author Xiao-Bai
 * @Date 2021/3/11 0011 20:43
 */
public enum ResultStatus {
    success(200,"success"),
    error(300,"error"),
    empty(100,"empty"),
    server_err(500,"server error");

    private Integer code;
    private String desc;

    ResultStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "ResultStatus{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                '}';
    }
}
