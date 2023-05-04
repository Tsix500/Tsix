package com.troublefixer.service;

import com.troublefixer.pojo.User;
import com.troublefixer.vo.Result;
import com.troublefixer.vo.params.LoginParams;

public interface UserService {
    User findUser(String account, String password);

//    Result findUserByToken(String token);

    User findUser(String account);

    Integer save(User user);

    Result modifyBasic(String token, LoginParams loginParams);

    Result modifyPassword(String token, LoginParams loginParams);

    Result getUserPage(Integer pagenum, Integer pagesize, Integer role, String nickName, String account);

    Result modifyUser(String token, User user);

    Result deleteUser(Long userId);
}
