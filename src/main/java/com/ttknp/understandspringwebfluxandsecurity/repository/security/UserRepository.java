package com.ttknp.understandspringwebfluxandsecurity.repository.security;

import com.ttknp.understandspringwebfluxandsecurity.model.security.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

// ** use @Service instead @Repository on service layer
public interface UserRepository extends ReactiveMongoRepository<User,Long> {
    // ** behind the sens ... where username = username auto
    Mono<User> findByUsername(String username);
}