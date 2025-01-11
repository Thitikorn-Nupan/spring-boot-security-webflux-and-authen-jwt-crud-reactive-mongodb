package com.ttknp.understandspringwebfluxandsecurity.repository;

import com.ttknp.understandspringwebfluxandsecurity.model.Post;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PostRepository extends ReactiveMongoRepository<Post, Long> { }
