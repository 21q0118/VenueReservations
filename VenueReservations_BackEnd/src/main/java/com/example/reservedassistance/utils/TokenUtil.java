package com.example.reservedassistance.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class TokenUtil {

    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    private static final long EXTENDED_EXPIRE_TIME = 14 * 24 * 60 * 60 * 1000;

    private static final String TOKEN_SECRET="tdjd";



    /**
     * 签名生成
     *
     * @param
     * @return
     */
    public static String sign(String telOrUserName, String password, Integer id, String role) {
        String token = null;
        try {
            Date expiresAt;

            expiresAt = new Date(System.currentTimeMillis() + EXPIRE_TIME);

            token = JWT.create()
                    .withIssuer("auth0")
//                    .withClaim("userId", user.getUserId())
                    .withClaim("telOrUserName", telOrUserName)
                    .withClaim("password", password)
                    .withClaim("id", id)
                    .withClaim("role", role)
                    .withExpiresAt(expiresAt)
                    // 使用了HMAC256加密算法。
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));
            System.out.println(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    /**
     * 签名验证
     *
     * @param token
     * @return
     */
    public static boolean verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
            DecodedJWT jwt = verifier.verify(token);

//            System.out.println("认证通过：");
//            System.out.println("username: " + jwt.getClaim("userName").asString());
//            System.out.println("username: " + jwt.getClaim("roleList"));
//            System.out.println("username: " + jwt.getClaim("permissionList"));
//            System.out.println("过期时间：      " + jwt.getExpiresAt());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int getId(String token){
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("id").asInt();
    }

    public static String getRole(String token){
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("role").asString();
    }

}
