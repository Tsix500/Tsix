package com.troublefixer.handler;

import com.alibaba.fastjson2.JSON;
import com.troublefixer.annotations.Privilige;
import com.troublefixer.vo.LoginUserVo;
import com.troublefixer.service.LoginService;
import com.troublefixer.utils.UserThreadLocal;
import com.troublefixer.vo.Result;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.troublefixer.vo.ErrorCode.NO_LOGIN;
import static com.troublefixer.vo.ErrorCode.PRIVILIGE_LOW;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {


    private final LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)){//是否是controller方法
            return true;
        }
        String token = request.getHeader("Authorization");
        if(StringUtils.isBlank(token)){
            Result result = Result.fail(NO_LOGIN.getCode(),NO_LOGIN.getMes());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        token = StringUtils.substringAfter(token, " ");
        LoginUserVo userVo = loginService.checkToken(token);
        if(userVo == null){
            Result result = Result.fail(NO_LOGIN.getCode(),NO_LOGIN.getMes());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        //放入用户线程
        UserThreadLocal.put(userVo);

        //获得权限注释，查看权限
        Privilige annotation =
                ((HandlerMethod) handler).getMethod().getDeclaringClass().getAnnotation(Privilige.class);
        //如果没有注释，直接放行
        if(annotation == null){
            return true;
        }
        //用户权限
        int role = userVo.getRole();
        //方法调用权限
        int value = annotation.value();
        //如果权限不够，返回错误
        if(role < value){
            Result result = Result.fail(PRIVILIGE_LOW.getCode(), PRIVILIGE_LOW.getMes());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserThreadLocal.remove();
    }
}
