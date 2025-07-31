package com.ttknp.understandspringwebfluxandsecurity.service;

import com.ttknp.understandspringwebfluxandsecurity.model.Post;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostService {
    Flux<Post> getPosts();
    Mono<Post> getPost(Long id);
    Mono<Post> createPost(Mono<Post> post);
    Mono<Boolean> updatePost(Long id, Mono<Post> post);
    Mono<Boolean> deletePost(Long id);
}
