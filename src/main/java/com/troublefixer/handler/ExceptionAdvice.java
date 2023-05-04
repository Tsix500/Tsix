package com.troublefixer.handler;

import com.troublefixer.vo.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.troublefixer.vo.ErrorCode.SYSTEM_ERROR;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler
    public Result doException(Exception e){
        e.printStackTrace();
        return Result.fail(SYSTEM_ERROR.getCode(),SYSTEM_ERROR.getMes());
    }
}
