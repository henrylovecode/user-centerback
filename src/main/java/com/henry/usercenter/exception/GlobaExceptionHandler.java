package com.henry.usercenter.exception;


import com.henry.usercenter.common.BaseResponse;
import com.henry.usercenter.common.ErrorCode;
import com.henry.usercenter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * FileName:     GlobaExceptionHandler
 * CreateBy:     IntelliJ IDEA
 * Author:       wei
 * Date:         2022-09-03
 * Description : 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobaExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error("businessException:" + e.getMessage(),e);
        return ResultUtils.error(e.getCode(), e.getMessage() , e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHnadler(RuntimeException e){
        log.error("runtimeException",e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }


}



