package com.troublefixer.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated//设置校验bean
@AllArgsConstructor
public class Result {
    private boolean flag;

    private Object data;

    @NotNull(message = "注入数据不能为空")//添加校验
    private String mes;

    private int code;

    public static Result fail(int code, String mes){
        return new Result(false, null, mes, code);
    }
    public static Result success(Object data){
        return new Result(true, data, "成功", 200);
    }
    public static Result success(Object data, String mes){
        return new Result(true, data, mes, 200);
    }
}
