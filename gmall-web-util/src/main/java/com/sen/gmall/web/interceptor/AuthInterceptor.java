package com.sen.gmall.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.sen.gmall.common.util.HttpclientUtil;
import com.sen.gmall.web.annotations.LoginRequire;
import com.sen.gmall.web.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取注解自定义注解的信息
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        LoginRequire annotation = handlerMethod.getMethodAnnotation(LoginRequire.class);

        //不需要验证
        if (annotation == null) {
            return true;
        }

        //验证token
        String token = "";
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        if (StringUtils.isNotBlank(oldToken)) {
            token = oldToken;
        }
        String newToken = request.getParameter("token");
        if (StringUtils.isNotBlank(newToken)) {
            token = newToken;
        }
        String success = "fail";
        Map<String, Object> map = new HashMap<>();
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ip)) {
            ip = request.getRemoteAddr();
            if (StringUtils.isBlank(ip)) {
                ip = "127.0.0.1";
            }
        }
        //通过验证中心验证token是否有效
        if (StringUtils.isNotBlank(token)) {
            String decodeMapStr = HttpclientUtil.doGet("http://localhost:8085/verify?token=" + token + "&currentIp=" + ip);
            map = JSON.parseObject(decodeMapStr, Map.class);
            if (map != null) {
                success = (String) map.get("status");
            }
        }
        //需要验证更具注解的值确定分支
        boolean loginSuccess = annotation.loginSuccess();
        //需要验证通过才能操作
        if (loginSuccess) {
            if ("success".equals(success)) {
                //往request中写入相关数据
                request.setAttribute("memberId", map.get("memberId"));
                request.setAttribute("nickname", map.get("nickname"));
            } else {
                //验证不通过踢回验证中心
                //验证通过回跳原地址
                response.sendRedirect("http://localhost:8085/login?returnUrl=" + request.getRequestURL());
                return false;
            }
            // 验证不通过也能操作
        } else {
            if ("success".equals(success)) {
                //往request中写入相关数据
                request.setAttribute("memberId",  map.get("memberId"));
                request.setAttribute("nickname",  map.get("nickname"));
            } else {
                return true;
            }
        }
        //更新cookie的token
        if (StringUtils.isNotBlank(token)) {
            CookieUtil.setCookie(request, response, "oldToken", token, 3600 * 7, true);
        }
        return true;
    }
}

