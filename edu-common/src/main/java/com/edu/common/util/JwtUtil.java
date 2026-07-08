package com.edu.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    private static final SecretKey KEY = Keys.hmacShaKeyFor("OnlineEducationPlatformSecretKey2026!!@#Secure".getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRE = 24 * 60 * 60 * 1000L;

    public static String generate(Long userId, Integer role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(KEY)
                .compact();
    }

    public static Claims parse(String token) {
        return Jwts.parser().verifyWith(KEY).build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static Long getUserId(String token) {
        return Long.parseLong(parse(token).getSubject());
    }

    public static Integer getRole(String token) {
        return parse(token).get("role", Integer.class);
    }
}
