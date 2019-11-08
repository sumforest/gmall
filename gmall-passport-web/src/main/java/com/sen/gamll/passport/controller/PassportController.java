package com.sen.gamll.passport.controller;

import com.sen.gmall.api.beans.UmsMember;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Auther: Sen
 * @Date: 2019/11/8 19:21
 * @Description:
 */
@Controller
public class PassportController {

    @GetMapping("login")
    public String toLogin(String returnUrl, ModelMap modelMap) {

        modelMap.put("returnUrl", returnUrl);
        return "login";
    }

    /**
     * 登录生成token
     * @param member
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public String login(UmsMember member) {
        //查询数据库，验证登录信息

        return "token";
    }

    @GetMapping("/verify")
    @ResponseBody
    public String verify(String token) {

        return "success";
    }

}
