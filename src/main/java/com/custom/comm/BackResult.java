package com.custom.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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


    public BackResult(Integer status, String msg, T data, Map<String, Object> attr) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.attr = attr;
    }
    public BackResult() {}

    public BackResult(String msg, T data, Map<String, Object> attr) {
        this.msg = msg;
        this.data = data;
        this.attr = attr;
    }

    public BackResult(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public BackResult(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public BackResult(String msg, T data) {
        this.msg = msg;
        this.data = data;
    }

    public BackResult(String msg) {
        this.msg = msg;
    }

    public static <T> BackResult<T> bySuccess(T data){
        logger.info("request-status: ---------" + ResultStatus.success.getDesc());
        return new BackResult<>(ResultStatus.success.getCode(), ResultStatus.success.getDesc(), data);
    }

    public static <T> BackResult<T> bySuccess(T data, Map<String, Object> attr){
        logger.info("request-status: ---------" + ResultStatus.success.getDesc());
        return new BackResult<>(ResultStatus.success.getCode(), ResultStatus.success.getDesc(), data, attr);
    }

    public static <T> BackResult<T> bySuccess(String msg, T data){
        logger.info("request-status: ---------" + ResultStatus.success.getDesc());
        return new BackResult<>(ResultStatus.success.getCode(), msg, data);
    }

    public static <T> BackResult<T> bySuccess(String msg){
        logger.info("request-status: ---------" + ResultStatus.success.getDesc());
        return new BackResult<>(ResultStatus.success.getCode(), msg);
    }



    public static <T> BackResult<T> byError(String msg, T data){
        logger.info("request-status: ---------" + ResultStatus.error.getDesc());
        return new BackResult<>(ResultStatus.error.getCode(), msg, data);
    }

    public static <T> BackResult<T> byError(T data){
        logger.info("request-status: ---------" + ResultStatus.error.getDesc());
        return new BackResult<>(ResultStatus.error.getCode(), ResultStatus.error.getDesc(), data);
    }

    public static <T> BackResult<T> byError(String msg){
        logger.info("request-status: ---------" + ResultStatus.error.getDesc());
        return new BackResult<>(ResultStatus.error.getCode(), msg);
    }



    public static <T> BackResult<T> byEmpty(String msg, T data){
        logger.info("request-status: ---------" + ResultStatus.empty.getDesc());
        return new BackResult<>(ResultStatus.empty.getCode(), msg, data);
    }

    public static <T> BackResult<T> byEmpty(T data){
        logger.info("request-status: ---------" + ResultStatus.empty.getDesc());
        return new BackResult<>(ResultStatus.empty.getCode(), ResultStatus.empty.getDesc(), data);
    }

    public static <T> BackResult<T> byEmpty(String msg){
        logger.info("request-status: ---------" + ResultStatus.empty.getDesc());
        return new BackResult<>(ResultStatus.empty.getCode(), msg);
    }



    public static <T> BackResult<T> byServerErr(String msg, T data){
        logger.info("request-status: ---------" + ResultStatus.server_err.getDesc());
        return new BackResult<>(ResultStatus.server_err.getCode(), msg, data);
    }

    public static <T> BackResult<T> byServerErr(String msg){
        logger.info("request-status: ---------" + ResultStatus.server_err.getDesc());
        return new BackResult<>(ResultStatus.server_err.getCode(), msg);
    }

    public interface Back<T>{
        void execCall(BackResult<T> backResult);
    }

    public static <T> BackResult<T> exeCall(BackResult.Back<T> back){
        BackResult<T> backResult = new BackResult<>();
        try {
            back.execCall(backResult);
        }catch (Exception e){
            logger.error(e.toString(), e);
            backResult = new BackResult<>(ResultStatus.error.getCode(), e.toString());
        }
        return backResult;
    }












}