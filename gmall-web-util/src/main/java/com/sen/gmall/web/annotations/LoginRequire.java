package com.sen.gmall.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Sen
 * @Date: 2019/11/8 21:41
 * @Description: 自定义登录拦截器注解
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface LoginRequire {

    /**
     * 是否需要成功登录
     * @return 默认需要成功登录
     */
    boolean loginSuccess() default true;
}
