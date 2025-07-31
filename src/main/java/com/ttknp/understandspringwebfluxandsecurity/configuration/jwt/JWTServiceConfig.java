package com.ttknp.understandspringwebfluxandsecurity.configuration.jwt;

import com.ttknp.understandspringwebfluxandsecurity.model.security.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// for generate jwt
@Component
public class JWTServiceConfig {

    //  private final long TIME_EXPIRY = 60 * 60 * 1000; // 1 hour
    private final String expirationTime;
    private final Key key;

    public JWTServiceConfig(@Value("${jwt.expiration}") String expirationTime,
                            @Value("${jwt.secret}") String secret) {
        this.expirationTime = expirationTime;
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }


    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token)
                .getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token)
                .getExpiration();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        /*
         by default payload
         {
          "sub": "admin",
          "iat": 1736425999,
          "exp": 1736429599
         }
         you can add some keys : values
         */
        claims.put("role", user.getRoles());
        return doGenerateToken(claims, user.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String username) {
        final Date createdDate = new Date(System.currentTimeMillis());
        final Date expirationDate = new Date(System.currentTimeMillis() + expirationTime); // + 1 h.
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(key)
                .compact();
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

}