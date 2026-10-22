package com.example.interview_helper_backend.hander;

import com.example.interview_helper_backend.exception.*;
import com.example.interview_helper_backend.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseBusinessException.class)
    public Result<Void> handleBaseBusinessException(BaseBusinessException e){
        //业务异常属可预期错误（如密码输错），只记简要WARN，避免ERROR堆栈淹没真实告警
        log.warn("业务异常，code: {}, 原因：{}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }
    //请求体JSON格式错误或字段校验失败（如必填项为空），归为请求参数问题，不走"系统繁忙"
    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public Result<Void> handleBadRequest(Exception e) {
        log.warn("请求参数错误: {}", e.getMessage());
        return Result.error(400, "请求参数错误，请检查后重试");
    }
    //缺少必填请求参数
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数: {}", e.getParameterName());
        return Result.error(400, "缺少必要参数：" + e.getParameterName());
    }
    //请求方法不匹配（如该用POST却发了GET）
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持: {}", e.getMethod());
        return Result.error(405, "请求方式不正确");
    }
    //兜底异常处理
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleGlobalException(Exception e) {
        log.error("系统内部异常: ", e);
        return Result.error(500, "系统繁忙，请稍后重试");
    }
}
