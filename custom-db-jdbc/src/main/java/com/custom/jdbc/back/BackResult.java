package com.custom.jdbc.back;

import com.custom.jdbc.utils.BackResultTransactionProxy;
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

    private static final Logger logger = LoggerFactory.getLogger(BackResult.class);

    /**
     * 状态码
     */
    private Integer status = ResultStatus.success.getCode();
    /**
     * 返回的消息
     */
    private String msg = ResultStatus.success.getDesc();
    /**
     * 响应数据
     */
    private T data;
    /**
     * 额外的响应数据
     */
    private Map<String, Object> attr = new HashMap<>();

    public BackResult(Integer status, String msg, T data, Map<String, Object> attr) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.attr = attr;
    }

    public BackResult(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

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

    public BackResult(String msg, T data, Map<String, Object> attr) {
        this.msg = msg;
        this.data = data;
        this.attr = attr;
    }


    public static <T> BackResult<T> bySuccess(T data){
        return new BackResult<>(ResultStatus.success.getCode(), ResultStatus.success.getDesc(), data);
    }

    public static <T> BackResult<T> bySuccess(T data, Map<String, Object> attr){
        return new BackResult<>(ResultStatus.success.getCode(), ResultStatus.success.getDesc(), data, attr);
    }

    public static <T> BackResult<T> bySuccess(String msg, T data){
        return new BackResult<>(ResultStatus.success.getCode(), msg, data);
    }

    public static <T> BackResult<T> bySuccess(String msg){
        return new BackResult<>(ResultStatus.success.getCode(), msg);
    }

    public static <T> BackResult<T> bySuccess(){
        return new BackResult<>();
    }



    public static <T> BackResult<T> byError(String msg, T data){
        return new BackResult<>(ResultStatus.error.getCode(), msg, data);
    }

    public static <T> BackResult<T> byError(int status, String msg){
        return new BackResult<>(status, msg);
    }

    public static <T> BackResult<T> byError(T data){
        return new BackResult<>(ResultStatus.error.getCode(), ResultStatus.error.getDesc(), data);
    }

    public static <T> BackResult<T> byError(String msg){
        return new BackResult<>(ResultStatus.error.getCode(), msg);
    }



    public static <T> BackResult<T> byEmpty(String msg, T data){
        return new BackResult<>(ResultStatus.empty.getCode(), msg, data);
    }

    public static <T> BackResult<T> byEmpty(T data){
        return new BackResult<>(ResultStatus.empty.getCode(), ResultStatus.empty.getDesc(), data);
    }

    public static <T> BackResult<T> byEmpty(String msg){
        return new BackResult<>(ResultStatus.empty.getCode(), msg);
    }

    public static <T> BackResult<T> byServerErr(String msg, T data){
        return new BackResult<>(ResultStatus.server_err.getCode(), msg, data);
    }

    public static <T> BackResult<T> byServerErr(String msg){
        return new BackResult<>(ResultStatus.server_err.getCode(), msg);
    }

    public interface Back<T>{
        void execCall(BackResult<T> backResult) throws Exception;
    }

    public static <T> BackResult<T> execCall(Back<T> back) {
        BackResult<T> backResult = new BackResult<>();
        try {
            BackResultTransactionProxy<T> transactionProxy = new BackResultTransactionProxy<>(back);
            Back<T> proxyBack = transactionProxy.getProxyBack();
            proxyBack.execCall(backResult);
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            backResult = new BackResult<>(ResultStatus.error.getCode(), e.getMessage());
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

    public void success(T data) {
        this.msg = ResultStatus.success.getDesc();
        this.status = ResultStatus.success.getCode();
        this.data = data;
    }

    public void success(T data, String msg) {
        this.msg = msg;
        this.status = ResultStatus.success.getCode();
        this.data = data;
    }

    public void success(T data, Map<String, Object> attr) {
        this.msg = ResultStatus.success.getDesc();
        this.status = ResultStatus.success.getCode();
        this.data = data;
        this.attr = attr;
    }


    public static void main(String[] args) throws Exception {

        BackResult<List<String>> test = test();
        System.out.println("test = " + test);

    }

    public static BackResult<List<String>> test() throws Exception {
        return BackResult.execCall(call -> {
            int a= 1;
            System.out.println(a);
            throw new NullPointerException("error....");
        });
    }








}
