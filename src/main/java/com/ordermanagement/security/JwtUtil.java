package com.ordermanagement.security;

import java.util.Date;

import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

    private static final String secrete_key = "gjvmg9759034fnlsdahfjkds875hgkfjdsh90sdfasfljnlnvgsaf798";
    
    private static final long EXPIRATION = 1000 * 60 * 60;

    private static final SecretKey key = Keys.hmacShaKeyFor(secrete_key.getBytes());

    public static String generateToken(String username,String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key)
                .compact();
    }

}
