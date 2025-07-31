package com.ttknp.understandspringwebfluxandsecurity.configuration.security;

import com.ttknp.understandspringwebfluxandsecurity.configuration.jwt.AuthenticationManagerServiceConfig;
import com.ttknp.understandspringwebfluxandsecurity.configuration.jwt.SecurityContextRepositoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

// ** The following page shows an explicit version of the minimal WebFlux Security configuration
@Configuration
@EnableWebFluxSecurity
public class SecurityWebConfig {

    private final AuthenticationManagerServiceConfig authenticationManagerServiceConfig;
    private final SecurityContextRepositoryConfig securityContextRepositoryConfig;

    @Autowired
    public SecurityWebConfig(AuthenticationManagerServiceConfig authenticationManagerServiceConfig, SecurityContextRepositoryConfig securityContextRepositoryConfig) {
        this.authenticationManagerServiceConfig = authenticationManagerServiceConfig;
        this.securityContextRepositoryConfig = securityContextRepositoryConfig;
    }

    // ** SecurityWebFilterChain for spring webflux ** it's kind of same config SecurityFilterChain
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // same pattern with SecurityFilterChain config
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.GET,"/api/posts/server").permitAll()
                        .pathMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
                        .pathMatchers(HttpMethod.GET,"/api/posts/search","/api/posts","/api/posts/").hasAnyRole("ADMIN", "USER")
                        .pathMatchers(HttpMethod.POST,"/api/posts/save").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT,"/api/posts/edit").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE,"/api/posts/remove").hasRole("ADMIN")
                        .anyExchange().authenticated() // all another authenticated without roles
                )
                .exceptionHandling()
                .authenticationEntryPoint(
                        (serverWebExchange, authenticationException) ->
                        Mono.fromRunnable(() -> serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                )
                .accessDeniedHandler((serverWebExchange, accessDeniedException) ->
                        Mono.fromRunnable(() -> serverWebExchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
                )
                .and()
                .csrf().disable() // ** this line work for allowed you can work with another http method as Post , Put , Delete
                .formLogin().disable() // ** disable basic form (ui) login
                .httpBasic().disable() // ** disable basic form (see on postman) authenticate
                .authenticationManager(authenticationManagerServiceConfig)
                .securityContextRepository(securityContextRepositoryConfig)
                .authorizeExchange()
                .and()
                .build();
    }
}
