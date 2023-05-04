package com.troublefixer.controller;

import com.troublefixer.service.LoginService;
import com.troublefixer.vo.Result;
import com.troublefixer.vo.params.LoginParams;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("regist")
@RequiredArgsConstructor
public class RegistController {

    private final LoginService loginService;

    @PostMapping
    public Result regist(@RequestBody LoginParams loginParams){
        return loginService.regist(loginParams);
    }
}
