package com.luodongseu.simpletask.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 全局统一处理异常
 *
 * @author luodongseu
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public GlobalException handleException(Exception e) {
        log.error("Unknown exception: ", e);
        return new GlobalException(ErrorCode.UNKNOWN_ERROR, e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullPointerException.class)
    public GlobalException handleNull(NullPointerException e) {
        log.error("NullPointerException exception: ", e);
        return new GlobalException(ErrorCode.REQUEST_INVALID,
                StringUtils.isEmpty(e.getMessage()) ? e.getMessage() : "不允许的空值");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullPointerException.class)
    public GlobalException handleNull(GlobalException e) {
        log.error("GlobalException exception: ", e);
        return e;
    }
}
