package com.troublefixer.service;

import com.troublefixer.vo.LoginUserVo;
import com.troublefixer.vo.Result;
import com.troublefixer.vo.params.LoginParams;

//@Transactional//加上事务
public interface LoginService {
    Result login(LoginParams loginParams);

    LoginUserVo checkToken(String token);

    Result logout(String token);

    Result regist(LoginParams loginParams);
}
