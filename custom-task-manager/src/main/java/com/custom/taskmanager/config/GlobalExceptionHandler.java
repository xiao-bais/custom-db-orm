package com.custom.taskmanager.config;

import com.custom.comm.utils.BackResult;
import com.custom.taskmanager.exception.BException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.StringJoiner;

/**
 * @Auther: Xiao-Bai
 * @Date: 2021/04/06/16:31
 * @Description:
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 全局异常
     */
    @ExceptionHandler(Exception.class)
    @Order(99)
    public BackResult exceptionHandler(Exception e){
        logger.error(e.toString(), e);
        return BackResult.byError("程序发生错误", e.toString());
    }

    /**
     * 自定义抛出的异常
     */
    @ExceptionHandler(BException.class)
    @Order(5)
    public BackResult eException(BException e) {
        if(e.getStatus() < 0) {
            return BackResult.byError(e.getStatus(), e.getMsg());
        }
        return BackResult.byError(e.getMsg(), e.getData());
    }

    /**
     * 绑定的请求参数异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Order(1)
    public BackResult methodArgumentNotValidException(MethodArgumentNotValidException e){
        StringJoiner exStr = new StringJoiner(",","","");
        List<FieldError> allErrors = e.getBindingResult().getFieldErrors();
        for (FieldError error : allErrors) {
            exStr.add(error.getDefaultMessage());
        }
        return BackResult.byEmpty(exStr);
    }
}
