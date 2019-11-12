package com.sen.gmall.gamll.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.sen.gmal.api.beans.UmsMember;
import com.sen.gmal.api.service.UmsMemberService;
import com.sen.gmall.common.util.HttpclientUtil;
import com.sen.gmall.web.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: Sen
 * @Date: 2019/11/8 19:21
 * @Description:
 */
@Controller
public class PassportController {

    @Reference
    private UmsMemberService umsMemberService;

    @GetMapping("login")
    public String toLogin(String returnUrl, ModelMap modelMap) {

        modelMap.put("returnUrl", returnUrl);
        return "login";
    }

    /**
     * 登录生成token
     *
     * @param member
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public String login(UmsMember member, HttpServletRequest request) {

        String token;
        //调用用户登录服务
        UmsMember umsMemberLogin = umsMemberService.login(member);
        if (umsMemberLogin != null) {
            token = createJWT(umsMemberLogin, request);
        } else {
            //登录失败
            token = "fail";
        }
        return token;
    }

    @GetMapping("/verify")
    @ResponseBody
    public String verify(String token, String currentIp) {
        Map<String, Object> map = JwtUtil.decode(token, "2019/11/9/15:25", DigestUtils.md5DigestAsHex(currentIp.getBytes()));
        if (map != null) {
            map.put("status", "success");
        } else {
            map = new HashMap<>();
            map.put("status", "fail");
        }
        return JSON.toJSONString(map);
    }

    /**
     * oauth2.0新浪微博登录
     *
     * @param code
     * @return
     */
    @GetMapping("vlogin")
    public String vlogin(String code,HttpServletRequest request) {
        String jwtToken = "fail";

        //用授权码获取token
        String url2 = "https://api.weibo.com/oauth2/access_token";
        //封装参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("client_id", "1016897975");
        paramMap.put("client_secret", "c114e7a5a69ab537a92d9546a63d785f");
        paramMap.put("grant_type", "authorization_code");
        paramMap.put("code", code);
        paramMap.put("redirect_uri", "http://127.0.0.1:8085/vlogin");
        String accessToken = HttpclientUtil.doPost(url2, paramMap);

        //用token获取新浪平台的用户信息
        Map<String, String> accessTokenMap = new HashMap<>();
        String url3 = "";
        if (StringUtils.isNotBlank(accessToken)) {
            accessTokenMap = JSON.parseObject(accessToken, Map.class);
            url3 = "https://api.weibo.com/2/users/show.json?access_token="+accessTokenMap.get("access_token")+"&uid="+accessTokenMap.get("uid");
        }

        UmsMember umsMember = new UmsMember();
        String userInfoJson = HttpclientUtil.doGet(url3);
        //保存了主要的用户信息
        if (StringUtils.isNotBlank(userInfoJson)) {
            Map<String, String> userMap = JSON.parseObject(userInfoJson, Map.class);
            String access_token = accessTokenMap.get("access_token");
            umsMember.setCity(userMap.get("location"));
            umsMember.setNickname(userMap.get("screen_name"));
            umsMember.setAccessCode(code);
            umsMember.setAccessToken(access_token);
            umsMember.setCreateTime(new Date());
            umsMember.setSourceType(2);
            umsMember.setSourceUid(userMap.get("idstr"));
            if ("m".equals(userMap.get("gender"))) {
                umsMember.setGender(1);
            } else if ("f".equals(userMap.get("gender"))) {
                umsMember.setGender(2);

            } else {
                umsMember.setGender(0);
            }
            //保存微博用户前先查询
            UmsMember authMember = umsMemberService.checkAuthMember(userMap.get("idstr"));
            if (authMember == null) {
                //不存在插入
                String id = umsMemberService.addMember(umsMember);
                umsMember.setId(id);
            } else {
                umsMember = authMember;
            }

            //生成jwt
            jwtToken = createJWT(umsMember, request);
        }
        return "redirect:http://localhost:8083/index?token=" + jwtToken;
    }

    private String createJWT(UmsMember umsMemberLogin,HttpServletRequest request) {
        //登录成功,生成token
        Map<String, Object> map = new HashMap<>();
        map.put("memberId", umsMemberLogin.getId());
        map.put("nickname", umsMemberLogin.getNickname());
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ip)) {
            ip = request.getRemoteAddr();
            if (StringUtils.isBlank(ip)) {
                ip = "127.0.0.1";
            }
        }

        String token = JwtUtil.encode("2019/11/9/15:25", map, DigestUtils.md5DigestAsHex(ip.getBytes()));
        //把token写入缓存
        umsMemberService.addToCache(umsMemberLogin.getId(), token);
        return token;
    }
}
