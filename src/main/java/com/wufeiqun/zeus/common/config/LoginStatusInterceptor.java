package com.wufeiqun.zeus.common.config;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.alibaba.fastjson2.JSON;
import com.wufeiqun.zeus.biz.system.UserFacade;
import com.wufeiqun.zeus.common.constant.GlobalConstant;
import com.wufeiqun.zeus.common.entity.CommonVo;
import com.wufeiqun.zeus.common.exception.ServiceException;
import com.wufeiqun.zeus.dao.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


import java.io.PrintWriter;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginStatusInterceptor implements HandlerInterceptor {
    private final UserFacade userFacade;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try{
            User user = getUserByToken(request);

            if (Objects.isNull(user)){
                log.warn("LoginStatusInterceptor.preHandle, user is null: {}", JSON.toJSONString(request));
                CommonVo<Object> commonVo = CommonVo.error("4001", "user is null");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter writer = response.getWriter();
                String jsonString = JSON.toJSONString(commonVo);
                writer.print(jsonString);
                writer.close();
                response.flushBuffer();
                return false;
            }

            if (!user.getStatus()){
                log.warn("LoginStatusInterceptor.preHandle, user is disabled: {}", JSON.toJSONString(user));
                CommonVo<Object> commonVo = CommonVo.error("4002", "用户已被禁用, 请联系管理员");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter writer = response.getWriter();
                String jsonString = JSON.toJSONString(commonVo);
                writer.print(jsonString);
                writer.close();
                response.flushBuffer();
                return false;
            }

            request.setAttribute(GlobalConstant.REQUEST_USER_KEY, user);
            return true;
        } catch (ServiceException e){
            CommonVo<Object> commonVo = CommonVo.error("4001", e.getMessage());
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer = response.getWriter();
            String jsonString = JSON.toJSONString(commonVo);
            writer.print(jsonString);
            writer.close();
            response.flushBuffer();
            return false;
        } catch (Exception e){
            log.warn("LoginStatusInterceptor.preHandle 异常", e);
            CommonVo<Object> commonVo = CommonVo.error("4001", "当前用户未登陆或token已失效");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer = response.getWriter();
            String jsonString = JSON.toJSONString(commonVo);
            writer.print(jsonString);
            writer.close();
            response.flushBuffer();
            return false;
        }
    }

    /**
     * 从请求header中拿到用户的token, 然后去用户中心/本地Redis换取用户的基本信息
     * 之前直接使用的企业微信返回的token给到了前端, 企业微信的token有效期仅为2小时,
     * 会造成频繁出发登录跳转操作,其实没必要还是使用自己之前的方式
     */
    private User getUserByToken(HttpServletRequest request) {
        User user;
        String token = request.getHeader("Authorization");

        if (StringUtils.isBlank(token)) {
            log.warn("getUserByToken, Header Authorization is null!");
            return null;
        }
        //
        user = useJWTMethod(token);
        return user;
    }

    private User useJWTMethod(String token){
        try{
            boolean status = JWTUtil.verify(token, GlobalConstant.JWT_TOKEN_SECRET.getBytes());
            if (!status){
                log.warn("getUserByToken, jwt verify failed!, token={}", token);
                return null;
            }
            JWT jwt = JWTUtil.parseToken(token);
            return userFacade.getUserByAccount((String)jwt.getPayload("account"));
        } catch (Exception e){
            log.warn("LoginStatusInterceptor.useJWTMethod 认证异常!", e);
            return null;
        }

    }

}
