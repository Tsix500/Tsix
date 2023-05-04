package com.troublefixer.controller;

import com.troublefixer.annotations.Privilige;
import com.troublefixer.pojo.User;
import com.troublefixer.service.UserService;
import com.troublefixer.vo.Result;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("sys/users")
public class SysUserController {

    private final UserService userService;

    //管理员查看用户列表
    @GetMapping({"getUserPage/{pagenum}/{pagesize}"})
    @Privilige(1)
    public Result getUserPage(@PathVariable("pagenum") Integer pagenum,
                              @PathVariable("pagesize") Integer pagesize,
                              @RequestParam(value = "role", required = false) Integer role,
                              @RequestParam(value = "nickName",required = false) String nickName,
                              @RequestParam(value = "account",required = false) String account){
        return userService.getUserPage(pagenum, pagesize, role, nickName,account);
    }

    @Privilige(1)
    @PatchMapping("modifyUser")
    public Result modifyUser(@RequestHeader("Authorization") String token, @RequestBody User user){
        token = StringUtils.substringAfter(token, " ");
        return userService.modifyUser(token, user);
    }

    @DeleteMapping("{userId}")
    @Privilige(1)
    public Result deleteUser(@PathVariable("userId") Long userId){
        return userService.deleteUser(userId);
    }
}
