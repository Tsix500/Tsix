package com.troublefixer.service.impl;

import com.alibaba.fastjson2.JSON;
import com.troublefixer.service.UserService;
import com.troublefixer.vo.LoginUserVo;
import com.troublefixer.vo.Result;
import com.troublefixer.pojo.User;
import com.troublefixer.service.LoginService;
import com.troublefixer.utils.JWTUtils;
import com.troublefixer.vo.params.LoginParams;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.troublefixer.vo.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserService userService;

    private final StringRedisTemplate redisTemplate;

    @Override
    public Result login(LoginParams loginParams) {
        String account = loginParams.getAccount();
        String password = loginParams.getPassword();
        if(StringUtils.isBlank(account)||StringUtils.isBlank(password)){
            return Result.fail(PARAM_Error.getCode(),PARAM_Error.getMes());
        }
        password = DigestUtils.md5Hex(password);
        User user = userService.findUser(account,password);
        if(user == null){
            return Result.fail(ACCOUNT_NOT_EXIST.getCode(),ACCOUNT_NOT_EXIST.getMes());
        }
        String token = JWTUtils.createToken(user.getUserId());
        LoginUserVo userVo = new LoginUserVo();
        userVo.setUserId(user.getUserId());
        userVo.setAccount(user.getAccount());
        userVo.setNickName(user.getNickName());
        userVo.setRole(user.getRole());
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(userVo),1, TimeUnit.DAYS);
        return Result.success(token);
    }

    @Override
    public LoginUserVo checkToken(String token) {
        if(StringUtils.isBlank(token)){
            return null;
        }
        Map<String, Object> map = JWTUtils.checkToken(token);
        if(map == null){
            return null;
        }
        String s = redisTemplate.opsForValue().getAndExpire("TOKEN_" + token, 1, TimeUnit.DAYS);
        LoginUserVo userVo = JSON.parseObject(s, LoginUserVo.class);
        return userVo;
    }

    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_" + token);
        return Result.success(null);
    }

    @Override
    public Result regist(LoginParams loginParams) {
        String account = loginParams.getAccount();
        String password = loginParams.getPassword();
        String nickName = loginParams.getNickName();
        if(StringUtils.isBlank(account)||StringUtils.isBlank(password)
                || StringUtils.isBlank(nickName)){
            return Result.fail(PARAM_Error.getCode(),PARAM_Error.getMes());
        }
        User user = userService.findUser(account);
        if(user != null){
            return Result.fail(ACCOUNT_EXIST.getCode(), ACCOUNT_EXIST.getMes());
        }
        user = new User();
        user.setAccount(account);
        user.setNickName(nickName);
        user.setPassword(DigestUtils.md5Hex(password));
        user.setRole(0);
        userService.save(user);

        return Result.success(null);
    }
}
