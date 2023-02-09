package com.custom.comm.utils.back;

/**
 * 返回结果状态
 * @author    Xiao-Bai
 * @since  2021/3/11 20:43
 */
public enum ResultStatus {
    success(2000,"success"),
    error(3000,"error"),
    empty(4000,"empty"),
    server_err(5000,"server error");

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
