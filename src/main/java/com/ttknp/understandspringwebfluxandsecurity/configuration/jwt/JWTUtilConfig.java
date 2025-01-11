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
public class JWTUtilConfig {

    private final long HOUR = 60 * 60 * 1000; // 1 hour
    private String secret;
    private String expirationTime;
    private Key key;

    public JWTUtilConfig(@Value("${springbootwebfluxjjwt.jjwt.expiration}")String expirationTime,
                         @Value("${springbootwebfluxjjwt.jjwt.secret}")String secret) {
        this.expirationTime = expirationTime;
        this.secret = secret;
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }


    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
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
         */
        // you can add some keys : values
        claims.put("role", user.getRoles());
        claims.put("username", user.getUsername());
        return doGenerateToken(claims, user.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String username) {
        final Date createdDate = new Date(System.currentTimeMillis());
        final Date expirationDate = new Date(System.currentTimeMillis() + HOUR); // + 1 h.
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