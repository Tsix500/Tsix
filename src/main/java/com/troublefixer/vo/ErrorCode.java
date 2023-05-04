package com.troublefixer.vo;

public enum ErrorCode {
    PARAM_Error(1000, "参数错误"),
    TOKEN_ERROR(1003,"TOKEN不合法"),
    ACCOUNT_EXIST(1004,"账户已存在"),
    NO_LOGIN(1005,"用户未登录"),
    ACCOUNT_NOT_EXIST(1001, "用户不存在"),
    NO_PERMISSION(7001, "无权限访问"),
    BOOK_ERROR(1002, "书籍不存在"),
    SYSTEM_ERROR(500,"服务器错误，请重试"),
    PRIVILIGE_ERROR(1006,"不能高于自己权限-_-"),
    FILETTPE_ERROR(1008,"文件类型错误"),
    PRIVILIGE_LOW(1007,"权限不足");

    private int code;
    private String mes;

    ErrorCode(int code, String mes){
        this.code = code;
        this.mes = mes;
    }

    public int getCode(){
        return code;
    }

    public String getMes(){
        return mes;
    }
}
