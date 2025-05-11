package com.xuecheng.base.exception;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@ControllerAdvice  // 全局异常处理
//@RestControllerAdvice // @RestControllerAdvice = @ControllerAdvice + @ResponseBody
public class GlobalExceptionHandler {
    @ResponseBody
    //对项目的自定义错误信息进行处理
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    @ExceptionHandler(XueChengPlusException.class)
    public RestErrorResponse handlerException(XueChengPlusException e) {
        //记录异常
        //解析错误信息
        String errMessage = e.getErrMessage();
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
    }
    //默认错误信息处理
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    @ExceptionHandler(Exception.class)
    public RestErrorResponse handler1Exception(Exception e) {
        //记录异常
        //解析错误信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
        return restErrorResponse;
    }

    //默认错误信息处理
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestErrorResponse handler2Exception(MethodArgumentNotValidException e) {
        //记录异常
        List<String> errs = new LinkedList<>();
        //解析错误信息
        BindingResult bindingResult = e.getBindingResult();//获取错误信息
        bindingResult.getFieldErrors().stream().forEach(item->
            errs.add(item.getDefaultMessage())
        );
        String errMessage = StringUtils.join(errs,","); // 拼接错误信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
    }
}