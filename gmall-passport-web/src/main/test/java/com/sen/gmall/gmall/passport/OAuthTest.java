package com.sen.gmall.gmall.passport;

import com.alibaba.fastjson.JSON;
import com.sen.gmall.gmall.common.util.HttpclientUtil;
import org.junit.Test;

import java.util.Map;

/**
 * @Auther: Sen
 * @Date: 2019/11/9 19:06
 * @Description:
 */
public class OAuthTest {

    @Test
    public void weiBoTest() {
        //App Key：1016897975
        // App Secret：c114e7a5a69ab537a92d9546a63d785f
        //1.请求微博授权页面
        String url1 = "https://api.weibo.com/oauth2/authorize?client_id=1016897975&response_type=code&redirect_uri=http://127.0.0.1:8085/vlogin";

        //2.获得回调页面和code
        // http://127.0.0.1:8085/vlogin?code=71850f4ff150353c723c92086fe5552f

        //3.通过post请求获取accesstoken
        // String url2 = "https://api.weibo.com/oauth2/access_token";
        // //封装参数
        // Map<String,String> paramMap = new HashMap<>();
        // paramMap.put("client_id", "1016897975");
        // paramMap.put("client_secret", "c114e7a5a69ab537a92d9546a63d785f");
        // paramMap.put("grant_type", "authorization_code");
        // paramMap.put("code", "71850f4ff150353c723c92086fe5552f");
        // paramMap.put("redirect_uri", "http://127.0.0.1:8085/vlogin");
        //
        // String accessToken = HttpclientUtil.doPost(url2, paramMap);
        // System.out.println(accessToken);
        // Map<String, String> accessTokenMap = JSON.parseObject(accessToken, Map.class);
        /*
            access_token	:	2.00b8HcAHPcnoGBb5a9eecda2vbgOOC
            remind_in	:	157679999
            expires_in	:	157679999
            uid	:	6422014907
            isRealName	:	true
         */

        //获取用户信息
        String url3 = "https://api.weibo.com/2/users/show.json?access_token=2.00b8HcAHPcnoGBb5a9eecda2vbgOOC&uid=6422014907";
        String userInfoJson = HttpclientUtil.doGet(url3);
        System.out.println(userInfoJson);
        Map<String, String> accessTokenMap = JSON.parseObject(userInfoJson, Map.class);
    }
}
