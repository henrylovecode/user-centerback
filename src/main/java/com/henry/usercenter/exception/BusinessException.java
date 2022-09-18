package com.henry.usercenter.exception;

import com.henry.usercenter.common.ErrorCode;

/**
 * FileName:     BusinessException
 * CreateBy:     IntelliJ IDEA
 * Author:       wei
 * Date:         2022-09-03
 * Description :
 */
public class BusinessException extends  RuntimeException {

    private final int code;

    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }


    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description =errorCode.getDescription();
    }


    public BusinessException(ErrorCode errorCode,String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
