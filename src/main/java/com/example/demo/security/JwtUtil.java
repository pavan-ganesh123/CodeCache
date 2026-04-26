package com.example.demo.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static final SecretKey key = Keys.hmacShaKeyFor(
        "mysecretkeymysecretkeymysecretkey".getBytes()
    );
    public static SecretKey getKey() {
        return key;
    }
    public static String generateToken(Long userId, String email) {
    return Jwts.builder()
            .setSubject(email)
            .claim("userId", userId)   
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(key)
            .compact();
    }
}