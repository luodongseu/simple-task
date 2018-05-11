package com.luodongseu.simpletask.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * 全局统一的异常
 *
 * @author luodongseu
 */
@Getter
@Setter
@JsonIgnoreProperties({"cause", "stackTrace", "localizedMessage", "suppressed", "e"})
public class GlobalException extends RuntimeException {

    /**
     * 错误码
     */
    private int errorCode;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 错误附属数据
     */
    private Object data;

    /**
     * 异常对象
     */
    private Exception e;

    public GlobalException(int errorCode, Exception e) {
        this(errorCode, e.getMessage(), e);
    }

    public GlobalException(int errorCode, String message, Exception e) {
        this(errorCode, message, null, e);
    }

    public GlobalException(int errorCode, String message, Object data, Exception e) {
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
        this.e = e;
    }

}
