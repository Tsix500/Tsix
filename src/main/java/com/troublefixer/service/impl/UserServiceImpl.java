package com.troublefixer.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.troublefixer.mapper.UserMapper;
import com.troublefixer.pojo.User;
import com.troublefixer.service.UserService;
import com.troublefixer.utils.UserThreadLocal;
import com.troublefixer.vo.LoginUserVo;
import com.troublefixer.vo.PageVo;
import com.troublefixer.vo.Result;
import com.troublefixer.vo.params.LoginParams;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.troublefixer.vo.ErrorCode.*;
import static com.troublefixer.vo.RoleName.ADMIN;
import static com.troublefixer.vo.RoleName.NOMAL;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final StringRedisTemplate redisTemplate;


    @Override
    public User findUser(String account, String password) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getAccount,account);
        lqw.eq(User::getPassword,password);
        lqw.select(User::getAccount,User::getNickName,User::getUserId,User::getRole);
        lqw.last("limit 1");
        return userMapper.selectOne(lqw);
    }


    @Override
    public User findUser(String account) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getAccount,account);
        lqw.select(User::getUserId);
        lqw.last("limit 1");
        return userMapper.selectOne(lqw);
    }

    @Override
    public Integer save(User user) {
        return userMapper.insert(user);
    }

    @Override
    public Result modifyBasic(String token, LoginParams loginParams) {
        LoginUserVo userVo = UserThreadLocal.get();
        String oldName = userVo.getNickName();
        String oldAccount = userVo.getAccount();
        String nickName = loginParams.getNickName();
        String account = loginParams.getAccount();
        if(StringUtils.isBlank(nickName)||StringUtils.isBlank(account)){
            return Result.fail(PARAM_Error.getCode(), PARAM_Error.getMes());
        }
        if(account.equals(oldAccount) && oldName.equals(nickName)){
            return Result.success(null);
        }
        //查询是否存在相同的账号
        User user = findUser(account);
        if(user != null && user.getUserId().compareTo(userVo.getUserId())!=0){
            return Result.fail(ACCOUNT_EXIST.getCode(), ACCOUNT_EXIST.getMes());
        }
        user = new User();
        user.setUserId(userVo.getUserId());
        user.setAccount(account);
        user.setNickName(nickName);

        userMapper.updateById(user);
        userVo.setAccount(account);
        userVo.setNickName(nickName);
        redisTemplate.opsForValue().set("TOKEN_" + token, JSON.toJSONString(userVo));
        return Result.success(null);
    }

    @Override
    public Result modifyPassword(String token, LoginParams loginParams) {
        String password = loginParams.getPassword();
        LoginUserVo userVo = UserThreadLocal.get();
        if(StringUtils.isBlank(password)){
            return Result.fail(PARAM_Error.getCode(), PARAM_Error.getMes());
        }
        User user = new User();
        user.setUserId(userVo.getUserId());
        user.setPassword(DigestUtils.md5Hex(password));
        userMapper.updateById(user);
        //退出登录清除缓存
        redisTemplate.delete("TOKEN_" + token);
        return Result.success(null);
    }

    @Override
    public Result getUserPage(Integer pagenum, Integer pagesize,
                              Integer role, String nickName, String account) {
        Page<User> page = new Page<>(pagenum, pagesize);
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper();
        lqw.select(User::getAccount,User::getNickName,User::getUserId,User::getRole);
        //权限不存在查看所有的用户
        if(role!=null && role <= ADMIN.getRole() && role >= NOMAL.getRole()){
            lqw.eq(User::getRole, role);
        }
        //查询相似昵称
        if(nickName != null && StringUtils.isNotBlank(nickName.trim())){
            lqw.like(User::getNickName, nickName);
        }
        //查询相似账号
        if(account != null && StringUtils.isNotBlank(account.trim())){
            lqw.like(User::getAccount, account);
        }
        Page<User> userPage = userMapper.selectPage(page, lqw);
        // 封装页面数据
        PageVo pageVo = new PageVo();
        pageVo.setTotal((int)userPage.getTotal());
        pageVo.setMaxpage((int)userPage.getPages());
        pageVo.setRecords(userPage.getRecords());
        return Result.success(pageVo);
    }

    /**
     * 管理员修改用户信息
     * @param token
     * @param user 包含id，role，account，nickName，password
     * @return
     */
    @Override
    public Result modifyUser(String token, User user) {
        Long userId = user.getUserId();
        String account = user.getAccount();
        String nickName = user.getNickName();
        Integer role = user.getRole();//修改的权限
        String password = user.getPassword();

        if(userId == null||role == null||StringUtils.isBlank(account.trim())
                ||StringUtils.isBlank(nickName.trim())||StringUtils.isBlank(password.trim())){
            return Result.fail(PARAM_Error.getCode(), PARAM_Error.getMes());
        }
        LoginUserVo userVo = UserThreadLocal.get();
        Integer userRole = userVo.getRole();//当前用户权限
        Integer oldRole = userMapper.selectRoleById(userId);//本来权限
        //不能修改比自己高的权限的用户
        if(role.compareTo(userRole) > 0 || oldRole.compareTo(userRole) > 0){
            return Result.fail(PRIVILIGE_ERROR.getCode(), PRIVILIGE_ERROR.getMes());
        }

        //查询是否存在相同的账号
        User user1 = findUser(account);
        if(user1 != null && user1.getUserId().compareTo(userId)!=0) {
            return Result.fail(ACCOUNT_EXIST.getCode(), ACCOUNT_EXIST.getMes());
        }

        //设置密码加密并更新
        user.setPassword(DigestUtils.md5Hex(password));
        userMapper.updateById(user);
        if(0 == userVo.getUserId().compareTo(user.getUserId())){
            redisTemplate.delete("TOKEN_" + token);
        }
        return Result.success(null);
    }

    @Override
    public Result deleteUser(Long userId) {
        Integer role = userMapper.selectRoleById(userId);
        LoginUserVo userVo = UserThreadLocal.get();
        Integer userRole = userVo.getRole();

        if(role == null || role.compareTo(userRole) <= 0){
            userMapper.deleteById(userId);
        }else{//不能删除比自己高的权限
            return Result.fail(PRIVILIGE_ERROR.getCode(), PRIVILIGE_ERROR.getMes());
        }
        return Result.success(null);
    }


    private User findUser(Long userId) {
        return userMapper.selectById(userId);
    }
}
