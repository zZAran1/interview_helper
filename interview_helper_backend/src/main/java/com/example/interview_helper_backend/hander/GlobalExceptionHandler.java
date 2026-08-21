package com.example.interview_helper_backend.hander;

import com.example.interview_helper_backend.exception.*;
import com.example.interview_helper_backend.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseBusinessException.class)
    public Result<Void> handleBaseBusinessException(BaseBusinessException e){
        log.error("业务异常，code: {}, 原因：{}", e.getCode(), e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }
    //兜底异常处理
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleGlobalException(Exception e) {
        log.error("系统内部异常: ", e);
        return Result.error(500, "系统繁忙，请稍后重试");
    }
}
