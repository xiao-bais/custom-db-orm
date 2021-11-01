package com.custom.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2021/3/11 0011 20:55
 */
public class BackResult<T> {

    private static Logger logger = LoggerFactory.getLogger(BackResult.class);

    //状态码
    private Integer status = ResultStatus.success.getCode();
    //返回的消息
    private String msg = ResultStatus.success.getDesc();
    //要返回的数据
    private T data;
    //额外数据
    private Map<String, Object> attr = new HashMap<>();

    public Integer getStatus() {
        return status;
    }

    public BackResult<T> setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public BackResult<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    @Override
    public String toString() {
        return "BackResult{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", attr=" + attr +
                '}';
    }

    public T getData() {
        return data;
    }

    public BackResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public Map<String, Object> getAttr() {
        return attr;
    }

    public BackResult<T> setAttr(Map<String, Object> attr) {
        this.attr = attr;
        return this;
    }

    public BackResult() {}

    public BackResult(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public interface Back<T>{
        void execCall(BackResult<T> backResult) throws Exception;
    }

    public static <T> BackResult<T> execCall(BackResult.Back<T> back){
        BackResult<T> backResult = new BackResult<>();
        try {
            back.execCall(backResult);
        }catch (Exception e){
            logger.error(e.toString(), e);
            backResult = new BackResult<>(ResultStatus.error.getCode(), e.toString());
        }
        return backResult;
    }

    public void error(String msg) {
        this.msg = msg;
        this.status = ResultStatus.error.getCode();
    }

    public void empty(String msg) {
        this.msg = msg;
        this.status = ResultStatus.empty.getCode();
    }

    public void success(String msg) {
        this.msg = msg;
        this.status = ResultStatus.success.getCode();
    }

    public void success(T data) {
        this.msg = ResultStatus.success.getDesc();
        this.status = ResultStatus.success.getCode();
        this.data = data;
    }

    public void success(T data, Map<String, Object> attr) {
        this.msg = ResultStatus.success.getDesc();
        this.status = ResultStatus.success.getCode();
        this.data = data;
        this.attr = attr;
    }


    public static void main(String[] args) {

        BackResult<List<String>> test = test();
        System.out.println("test = " + test);

    }

    public static BackResult<List<String>> test(){
        return BackResult.execCall(call -> {
           call.error("错误了");

        });
    }








}
