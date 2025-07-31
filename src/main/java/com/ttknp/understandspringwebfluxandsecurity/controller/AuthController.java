package com.ttknp.understandspringwebfluxandsecurity.controller;

import com.ttknp.responsecustomservice.constant.CommonStatus;
import com.ttknp.responsecustomservice.entity.ResponseObject;
import com.ttknp.understandspringwebfluxandsecurity.configuration.jwt.JWTServiceConfig;
import com.ttknp.understandspringwebfluxandsecurity.logging.Logback;
import com.ttknp.understandspringwebfluxandsecurity.service.security.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {

    // **
    private final JWTServiceConfig jwtServiceConfig;
    private final UserService userService;
    private final Logback logback;

    // ** This case getter/setter for mapping to json because i set attribute as private
    // ** Note! non-static inner classes like this can only by instantiated using default, no-argument constructor
    // ** fix by changing class to static class
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class AuthRequest {
        private String username;
        private String password;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class AuthResponse {
        private String token;
    }

    @Autowired
    private PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    };

    @Autowired
    public AuthController(JWTServiceConfig jwtServiceConfig, UserService userService) {
        this.jwtServiceConfig = jwtServiceConfig;
        this.userService = userService;
        logback = new Logback(AuthController.class);
    }

    @PostMapping(value = "/login")
    private Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
        // logback.log.debug("logging in {}", authRequest);
       return userService.searchByUsername(authRequest.username)
                .map(userDetails -> {
                    AuthResponse authResponse = new AuthResponse();
                    authResponse.token = jwtServiceConfig.generateToken(userDetails);
                    if (!getPasswordEncoder().matches(authRequest.password, userDetails.getPassword())) { // encoded password
                        authResponse = new AuthResponse();
                    }
                    return ResponseEntity
                            .status((short) CommonStatus.ACCEPTED[0])
                            .body(authResponse);
                }).switchIfEmpty( // case on found user
                        Mono.just(ResponseEntity
                       .status((short) CommonStatus.ACCEPTED[0])
                       .body(new AuthResponse()))
               );

    }

    // *** auth password that's a Bcrypt code
    /**
    @GetMapping(value = "/test")
    public Mono<ResponseEntity<AuthResponse>> login() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.username = "user";
        authRequest.password = "12345";
        User user = new User(1L,"user","12345","");
        logback.log.debug("logging in {}", authRequest);
        return userService.searchByUsername(authRequest.username)
                .map(userDetails -> {
                    AuthResponse authResponse = new AuthResponse();
                    authResponse.token = jwtUtilConfig.generateToken(userDetails);
                    // if (userDetails.getPassword().equals(authRequest.password)) {
                    if (getPasswordEncoder().matches(authRequest.password, userDetails.getPassword())) {
                        return ResponseEntity.ok(authResponse);
                    } else {
                        throw new BadCredentialsException("Invalid username or password");
                    }
                }).switchIfEmpty(Mono.error(new BadCredentialsException("Invalid username or password")));
    }
    @GetMapping(value = "/test2")
    public Mono<ResponseEntity<AuthResponse>> login2() {
        User user = new User(1L,"admin","12345","");
        return userService.searchByUsername(user.getUsername())
                .filter(userDetails -> getPasswordEncoder().matches(user.getPassword(), userDetails.getPassword()))
                .map(userDetails -> ResponseEntity.ok(new AuthResponse(jwtUtilConfig.generateToken(userDetails))))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }*/
    // *** same result


    /**
        // *** auth password that's a plain text code
        @GetMapping(value = "/test")
        public Mono<ResponseEntity<AuthResponse>> login() {
            AuthRequest authRequest = new AuthRequest();
            authRequest.username = "user";
            authRequest.password = "12345";
            logback.log.debug("logging in {}", authRequest);
            return userService.read(authRequest.username)
                    .map(userDetails -> {
                        AuthResponse authResponse = new AuthResponse();
                        authResponse.token = jwtUtil.generateToken(authRequest.username);
                         if (userDetails.getPassword().equals(authRequest.password)) {
                            return ResponseEntity.ok(authResponse);
                        } else {
                            throw new BadCredentialsException("Invalid username or password");
                        }
                    }).switchIfEmpty(Mono.error(new BadCredentialsException("Invalid username or password")));
        }
      */

}
