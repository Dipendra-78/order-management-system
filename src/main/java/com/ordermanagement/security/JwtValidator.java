package com.ordermanagement.security;

import javax.crypto.SecretKey;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtValidator {

    private static final String secrete_key="gjvmg9759034fnlsdahfjkds875hgkfjdsh90sdfasfljnlnvgsaf798";

    private static final SecretKey key= Keys.hmacShaKeyFor(secrete_key.getBytes());

    public static String extractUsername(String token)
    {
        Claims claims= Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

            return claims.getSubject();
    }

    

}
