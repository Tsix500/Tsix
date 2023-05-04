package com.troublefixer.controller;

import com.troublefixer.annotations.Privilige;
import com.troublefixer.pojo.User;
import com.troublefixer.service.UserService;
import com.troublefixer.utils.UserThreadLocal;
import com.troublefixer.vo.Result;
import com.troublefixer.vo.params.LoginParams;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("currentUser")
    public Result currentUser(){
        return Result.success(UserThreadLocal.get());
    }

    @PatchMapping("modifyBasic")
    public Result modifyUserBasic(@RequestHeader("Authorization") String token,@RequestBody LoginParams loginParams){
        token = StringUtils.substringAfter(token, " ");
        return userService.modifyBasic(token, loginParams);
    }

    @PatchMapping("modifySecret")
    public Result modifyUserPassword(@RequestHeader("Authorization") String token,@RequestBody LoginParams loginParams){
        token = StringUtils.substringAfter(token, " ");
        return userService.modifyPassword(token, loginParams);
    }


}
