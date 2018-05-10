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
@JsonIgnoreProperties({"cause", "stackTrace", "localizedMessage", "suppressed"})
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

    public GlobalException(int errorCode) {
        this(errorCode, "");
    }

    public GlobalException(int errorCode, String message) {
        this(errorCode, message, null);
    }

    public GlobalException(int errorCode, String message, Object data) {
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
    }

}
