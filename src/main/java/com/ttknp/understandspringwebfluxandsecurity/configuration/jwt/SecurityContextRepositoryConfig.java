package com.ttknp.understandspringwebfluxandsecurity.configuration.jwt;


import com.ttknp.understandspringwebfluxandsecurity.logging.Logback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// ** ServerSecurityContextRepository for get the token and forward to AuthenticationManager class
@Component
public class SecurityContextRepositoryConfig implements ServerSecurityContextRepository {

    private final AuthenticationManagerServiceConfig authenticationManagerServiceConfig;
    private final Logback logback;

    @Autowired
    public SecurityContextRepositoryConfig(AuthenticationManagerServiceConfig authenticationManagerServiceConfig) {
        this.authenticationManagerServiceConfig = authenticationManagerServiceConfig;
        logback = new Logback(SecurityContextRepositoryConfig.class);
    }

    @Override
    public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        final String AUTHORIZATION  = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return Mono.justOrEmpty(AUTHORIZATION)
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .flatMap(authHeader -> {
                    String token = authHeader.substring(7);
                    Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
                    logback.log.debug("Authenticated user (auth.getPrincipal()) : {}", auth.getPrincipal()); // return your claims as token
                    return this.authenticationManagerServiceConfig.authenticate(auth).map(SecurityContextImpl::new);
                });
    }
}