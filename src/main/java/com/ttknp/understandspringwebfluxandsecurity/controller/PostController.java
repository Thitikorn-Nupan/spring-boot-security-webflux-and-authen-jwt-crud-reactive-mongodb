package com.ttknp.understandspringwebfluxandsecurity.controller;

import com.ttknp.understandspringwebfluxandsecurity.logging.Logback;
import com.ttknp.understandspringwebfluxandsecurity.model.Post;
import com.ttknp.understandspringwebfluxandsecurity.service.PostService;
import com.ttknpdev.constant.CommonStatus;
import com.ttknpdev.entity.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api")
public class PostController {

    private final PostService postService;
    private Logback logback;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
        logback = new Logback(PostController.class);
    }

    @GetMapping(value = "/server")
    private ResponseEntity<ResponseObject> server() {
        return ResponseEntity.ofNullable(ResponseObject.<String>builder()
                .status((short) CommonStatus.OK[0])
                .info((String) CommonStatus.OK[1])
                .data("server is running")
                .build()
        );
    }

    // ** Note You cant return a nested flux in a dto You should return a Flux<WorkerDto>
    /**
        ex response
        {
            "status": 200,
            "info": "ok",
            "data": {
                "scanAvailable": true,
                "prefetch": -1
            }
        }
     */
    //
    //        /**
    //         ** No working
    //         @GetMapping(value = "/posts")
    //         private ResponseEntity<ResponseObject> reads() {
    //         cant return a nested flux in a dto
    //         return ResponseEntity
    //                .ok(ResponseObject.<Flux<Post>>builder()
    //                        .status((short) CommonStatus.OK[0])
    //                        .info((String) CommonStatus.OK[1])
    //                        .data(postService.getPosts())
    //                        .build());
    //         }
    //         */
    //    }
    @GetMapping(value = "/posts")
    private Mono<ResponseEntity<Flux<Post>>> reads() {
        logback.log.debug("posts stores {}",postService.getPosts());
        // ResponseEntity.status(200).body(postService.getPosts())
        return Mono.just(ResponseEntity.status(200).body(postService.getPosts()));
    }
    /**
     still work
    [
        {
            "id": 3,
            "topic": "reading comics book",
            "details": "how often did you read the comics book? tell me a little bit!",
            "datetime": "2025-01-07T12:17:21.321+00:00"
        },
        {
            "id": 1,
            "topic": "test",
            "details": "test",
            "datetime": "2025-01-09T07:27:41.124+00:00"
        }
    ]
    */

    @GetMapping(value = "/post")
    private ResponseEntity<Mono<Post>> read(@RequestParam long id) {
        return ResponseEntity
                .status(200)
                .body(postService.getPost(id));
    }

    @PostMapping(value = "/post")
    private ResponseEntity<Mono<Post>> create(@RequestBody Mono<Post> postMono) {
        return ResponseEntity.status(201).body(postService.createPost(postMono));
    }

    @PutMapping(value = "/post")
    private ResponseEntity<Mono<Boolean>> update(@RequestBody Mono<Post> postMono, @RequestParam long id) {
        return ResponseEntity.status(202).body(postService.updatePost(id,postMono));
    }

    @DeleteMapping(value = "/post")
    private ResponseEntity<Mono<Boolean>> delete( @RequestParam long id) {
        return ResponseEntity.status(202).body(postService.deletePost(id));
    }

    @GetMapping("/protected")
    public Mono<ResponseEntity<String>> protectedEndpoint() {
        return Mono.just(ResponseEntity.ok("You have accessed a protected endpoint!"));
    }
}
