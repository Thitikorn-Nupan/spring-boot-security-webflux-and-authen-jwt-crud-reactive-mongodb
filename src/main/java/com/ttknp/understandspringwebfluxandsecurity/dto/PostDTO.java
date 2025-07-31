package com.ttknp.understandspringwebfluxandsecurity.dto;

import com.ttknp.understandspringwebfluxandsecurity.logging.Logback;
import com.ttknp.understandspringwebfluxandsecurity.model.Post;
import com.ttknp.understandspringwebfluxandsecurity.repository.PostRepository;
import com.ttknp.understandspringwebfluxandsecurity.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class PostDTO implements PostService {

    private final PostRepository postRepository;
    private final Logback logback;

    @Autowired
    public PostDTO(PostRepository postRepository) {
        this.postRepository = postRepository;
        logback = new Logback(PostDTO.class);
    }

    @Override
    public Flux<Post> getPosts() {
        return postRepository.findAll();
    }

    @Override
    public Mono<Post> getPost(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Mono<Post> createPost(Mono<Post> postMono) {
        return postMono.flatMap((post) -> {
            post.setDatetime(new Date());
            return postRepository.save(post);
        });
    }

    @Override
    public Mono<Boolean> updatePost(Long id, Mono<Post> postMono) {
        return postMono.flatMap( (post) -> {
            return postRepository.findById(id).flatMap((postSearch) -> {
                post.setId(postSearch.getId());
                post.setDatetime(postSearch.getDatetime());
                // logback.log.debug("post exists {}", post);
                return postRepository.save(post).then(Mono.just(true));
            }).hasElement(); // it will return false if didn't found id
        });
    }

    @Override
    public Mono<Boolean> deletePost(Long id) {
            return postRepository.findById(id).flatMap((postSearch) -> {
                return postRepository.delete(postSearch).then(Mono.just(true));
            }).hasElement(); // it will return false if didn't found id
    }


}
