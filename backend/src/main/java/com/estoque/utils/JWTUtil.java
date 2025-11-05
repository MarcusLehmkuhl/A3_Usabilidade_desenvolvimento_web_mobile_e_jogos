package com.estoque.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.Date;
import java.util.Properties;

public class JWTUtil {
    private static String SECRET_KEY;
    private static Long EXPIRATION_TIME;
    private static Key key;

    static {
        try {
            Properties props = new Properties();
            InputStream input = JWTUtil.class.getClassLoader()
                    .getResourceAsStream("database.properties");
            
            if (input != null) {
                props.load(input);
                SECRET_KEY = props.getProperty("jwt.secret");
                EXPIRATION_TIME = Long.parseLong(props.getProperty("jwt.expiration"));
                key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateToken(String userId, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    public static boolean isTokenValid(String token) {
        try {
            Claims claims = validateToken(token);
            return claims != null && claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
