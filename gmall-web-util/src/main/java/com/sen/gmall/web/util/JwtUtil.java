package com.sen.gmall.web.util;

import io.jsonwebtoken.*;
import java.util.Map;

public class JwtUtil {
    /**
     * jwt加密
     * @param key 私钥
     * @param param 参数
     * @param salt 盐值
     * @return jwt字符串
     */
    public static String encode(String key, Map<String,Object> param, String salt){
        if(salt!=null){
            key+=salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256,key);
        jwtBuilder = jwtBuilder.setClaims(param);

        return jwtBuilder.compact();
    }


    /**
     * 解密jwt
     * @param token 密钥
     * @param key 私钥
     * @param salt 盐值
     * @return
     */
    public  static Map<String,Object>  decode(String token ,String key,String salt){
        Claims claims;
        if (salt!=null){
            key+=salt;
        }
        try {
            claims= Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch ( JwtException e) {
           return null;
        }
        return  claims;
    }
}
