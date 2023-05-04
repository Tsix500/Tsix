package com.troublefixer.controller;

import com.troublefixer.service.LoginService;
import com.troublefixer.vo.Result;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("logout")
@RequiredArgsConstructor
public class LogoutController {

    private final LoginService loginService;

    @GetMapping
    public Result logout(@RequestHeader("Authorization") String token){
        token = StringUtils.substringAfter(token, " ");
        return loginService.logout(token);
    }
}
