package com.wufeiqun.zeus.common.utils;

import com.wufeiqun.zeus.common.constant.GlobalConstant;
import com.wufeiqun.zeus.dao.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public class RequestUtil {

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null)    return null;
        for (Cookie ck : cookies) {
            if (StringUtils.equalsIgnoreCase(name,ck.getName()))
                return ck.getValue();
        }
        return null;
    }

    public static HttpServletRequest getRequest(){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return servletRequestAttributes.getRequest();
    }

    public static HttpServletResponse getResponse(){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return servletRequestAttributes.getResponse();
    }

    public static User getCurrentUser() {
        return (User) getRequest().getAttribute(GlobalConstant.REQUEST_USER_KEY);
    }
}
