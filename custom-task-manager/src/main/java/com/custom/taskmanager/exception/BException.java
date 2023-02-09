package com.custom.taskmanager.exception;

/**
 * @Auther: Xiao-Bai
 * @since : 2021/04/08/17:08
 * @Description:
 */
public class BException extends RuntimeException {

    private int status = 0;

    private String msg;

    private Object data;

    public BException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public BException(int status, String msg) {
        super(msg);
        this.status = status;
        this.msg = msg;
    }

    public BException(String msg, Object data) {
        super(msg);
        this.msg = msg;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
