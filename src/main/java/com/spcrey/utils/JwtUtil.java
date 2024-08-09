package com.spcrey.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JwtUtil {
    private static final String KRY = "firefly-in-dream";
    public static String genToken(Map<String, Object> claims) {
        return JWT.create()
            .withClaim("claims", claims)
            .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
            .sign(Algorithm.HMAC256(KRY));
    }
    public static Map<String, Object> parseToken(String token) {
        return JWT.require(Algorithm.HMAC256(KRY))
            .build()
            .verify(token)
            .getClaim("claims")
            .asMap();
    }
    public static void main(String[] args) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 17);
        claims.put("username", "spcrey");
        String token = genToken(claims);
        System.out.println("orifgin claims:" + claims);
        System.out.println("token: " + token);
        System.out.println("parse claims: " + parseToken(token));
    }
}
