package com.example.reservedassistance.utils;

/**
 * @project: ssm_sms
 * @description: 绘制验证码图片
 */

import com.example.reservedassistance.entity.Manager;
import com.example.reservedassistance.entity.User;
import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

public class JwtHelper {
    private static long tokenExpiration = 5 * 60 * 60 * 1000;//;
    private static String tokenSignKey = "123456";

    //生成token字符串
    public static String createToken(Integer id, String phone, String username, String role, String qqEmail) {
        String token = Jwts.builder()
                .setSubject("YYGH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("id", id)
                .claim("phone", phone)
//                .claim("username", username)
//                .claim("qqEmail", qqEmail)
//                .claim("role", role)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    public static String createToken(User user, String role) {
        String token = Jwts.builder()
                .setSubject("YYGH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("id", user.getId())
                .claim("phone", user.getTelephone())
                .claim("username", user.getUserName())
                .claim("qqEmail", user.getEmail())
                .claim("role", role)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    public static String createToken(Manager manager, String role) {
        String token = Jwts.builder()
                .setSubject("YYGH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("id", manager.getId())
                .claim("username", manager.getManagerUserName())
                .claim("isSuper", manager.getIsSuper())
                .claim("role", role)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    // 从token字符串获取userid
    public static Integer getUserId(String token) {
        if (StringUtils.isEmpty(token)) return null;
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        Integer userId = (Integer) claims.get("id");
        return userId;
    }

    // 从token字符串获取phone
    public static String getUserPhone(String token) {
        if (StringUtils.isEmpty(token)) return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        String phone = (String) claims.get("phone");
        return phone;
    }

    //从token字符串获取userType
    public static String getUserRole(String token) {
        if (StringUtils.isEmpty(token)) return "";
        Jws<Claims> claimsJws
                = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("role");
    }

    //从token字符串获取userName
    public static String getUserName(String token) {
        if (StringUtils.isEmpty(token)) return "";
        Jws<Claims> claimsJws
                = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("username");
    }

    //从token字符串获取qqEmail
    public static String getUserQqEmail(String token) {
        if (StringUtils.isEmpty(token)) return "";
        Jws<Claims> claimsJws
                = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("qqEmail");
    }

    //判断token是否有效
    public static boolean isExpiration(String token) {
        try {
            boolean isExpire = Jwts.parser()
                    .setSigningKey(tokenSignKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration().before(new Date());
            //没有过期，有效，返回false
            return isExpire;
        } catch (Exception e) {
            //过期出现异常，返回true
            return true;
        }
    }


    /**
     * 刷新Token
     *
     * @param token
     * @return
     */
    public String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = Jwts.parser()
                    .setSigningKey(tokenSignKey)
                    .parseClaimsJws(token)
                    .getBody();
            refreshedToken = JwtHelper.createToken(getUserId(token), getUserPhone(token), getUserName(token), getUserRole(token), getUserQqEmail(token));
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    // 从token字符串获取user
//    public static User getUserByToken(String token) throws Exception {
//        if (StringUtils.isEmpty(token)) return null;
//        Jws<Claims> claimsJws
//                = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
//        Claims claims = claimsJws.getBody();
//        User user = new User();
//
//        user.setId((Integer) claims.get("id"));
//        user.set((String) claims.get("phone"));
//        user.setUsername((String) claims.get("username"));
//        user.setQqEmail((String) claims.get("qqEmail"));
//
//        return user;
//    }

//    public static void main(String[] args) {
//        String token = createToken(8,"19856335525","19856335525","","");
//        Integer userId = getUserId(token);
//        System.out.println(userId);
//        String phone = getUserPhone(token);
//        System.out.println(phone);
//        String role = getUserRole(token);
//        System.out.println(role);
//        String username = getUserName(token);
//        System.out.println(username);
//        String qqEmail = getUserQqEmail(token);
//        System.out.println(qqEmail);
//        try {
//            User user = getUserByToken(token);
//            System.out.println(user);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(token);
//    }
}