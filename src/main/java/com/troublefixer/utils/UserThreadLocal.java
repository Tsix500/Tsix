package com.troublefixer.utils;

import com.troublefixer.vo.LoginUserVo;

public class UserThreadLocal {

    private UserThreadLocal(){};

    private static final ThreadLocal<LoginUserVo> LOCAL= new ThreadLocal<>();

    public static void put(LoginUserVo userVo){
        LOCAL.set(userVo);
    }

    public static LoginUserVo get(){
        return LOCAL.get();
    }

    public static void remove(){
        LOCAL.remove();
    }
}
