package com.ttknp.understandspringwebfluxandsecurity.configuration.jwt;

import com.ttknp.understandspringwebfluxandsecurity.logging.Logback;
import io.jsonwebtoken.Claims;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
    ReactiveAuthenticationManager for validate token and role
*/
@Component
public class AuthenticationManagerConfig implements ReactiveAuthenticationManager {

    private JWTUtilConfig jwtUtilConfig;
    private Logback logback;

    @Autowired
    public AuthenticationManagerConfig(JWTUtilConfig jwtUtilConfig) {
        this.jwtUtilConfig = jwtUtilConfig;
        logback = new Logback(AuthenticationManagerConfig.class);
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        logback.log.debug("authenticate(Authentication) method called Authenticating {}", authentication.getPrincipal());
        String authToken = authentication.getCredentials().toString();
        String username = jwtUtilConfig.getUsernameFromToken(authToken);
        return Mono
                .just(jwtUtilConfig.validateToken(authToken))
                .filter(valid -> valid)
                .switchIfEmpty(Mono.empty())
                .map(valid -> {
                    Claims claims = jwtUtilConfig.getAllClaimsFromToken(authToken);
                    // ** Not a good way if user has more one role
                    // List<String> rolesMap = new ArrayList<>();
                    // rolesMap.add(claims.get("role").toString());
                    // ** Both
                    // same claims on generateToken(User) method
                    List<SimpleGrantedAuthority> roles  = List.of(new SimpleGrantedAuthority(claims.get("role").toString()));
                    return new UsernamePasswordAuthenticationToken(username, null, roles);
                });
    }
}