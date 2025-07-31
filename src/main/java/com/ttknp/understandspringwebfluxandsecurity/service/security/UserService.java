package com.ttknp.understandspringwebfluxandsecurity.service.security;

import com.ttknp.understandspringwebfluxandsecurity.model.security.User;
import com.ttknp.understandspringwebfluxandsecurity.repository.security.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

// *** No need DTO layer in this case ** work only login
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository repository) {
        this.userRepository = repository;
    }

    public Mono<User> searchByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}