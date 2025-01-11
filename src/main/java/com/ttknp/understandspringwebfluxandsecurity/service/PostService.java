package com.ttknp.understandspringwebfluxandsecurity.service;

import com.ttknp.understandspringwebfluxandsecurity.model.Post;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostService {
    public Flux<Post> getPosts();
    public Mono<Post> getPost(Long id);
    public Mono<Post> createPost(Mono<Post> post);
    public Mono<Boolean> updatePost(Long id, Mono<Post> post);
    public Mono<Boolean> deletePost(Long id);
}
