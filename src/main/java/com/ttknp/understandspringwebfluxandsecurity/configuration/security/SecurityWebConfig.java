package com.ttknp.understandspringwebfluxandsecurity.configuration.security;

import com.ttknp.understandspringwebfluxandsecurity.configuration.jwt.AuthenticationManagerConfig;
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

    private AuthenticationManagerConfig authenticationManagerConfig;
    private SecurityContextRepositoryConfig securityContextRepositoryConfig;

    @Autowired
    public SecurityWebConfig(AuthenticationManagerConfig authenticationManagerConfig, SecurityContextRepositoryConfig securityContextRepositoryConfig) {
        this.authenticationManagerConfig = authenticationManagerConfig;
        this.securityContextRepositoryConfig = securityContextRepositoryConfig;
    }

    // ** SecurityWebFilterChain for spring webflux
    // ** it's kind of same config SecurityFilterChain
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // same pattern with SecurityFilterChain config
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.GET,"/api/server","/api/auth/login").permitAll()
                        .pathMatchers(HttpMethod.GET,"/api/auth/login").permitAll()
                        .pathMatchers(HttpMethod.GET,"/api/post","api/posts").hasAnyRole("ADMIN", "USER")
                        .pathMatchers(HttpMethod.POST,"/api/post").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT,"/api/post").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE,"/api/post").hasRole("ADMIN")
                        .anyExchange().authenticated()
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
                .csrf().disable() // this line work for allowed you can work with another http method as Post , Put , Delete
                .formLogin().disable() // disable basic form (ui) login
                .httpBasic().disable() // disable basic form (see on postman) authenticate
                .authenticationManager(authenticationManagerConfig)
                .securityContextRepository(securityContextRepositoryConfig)
                .authorizeExchange()
                .and()
                .build();
        /*
         // have to disable if you have worked with jwt authenticate
         .httpBasic(withDefaults()) // using basic authenticate
         .formLogin(withDefaults()) // using basic form (ui) login
         */
    }
}
