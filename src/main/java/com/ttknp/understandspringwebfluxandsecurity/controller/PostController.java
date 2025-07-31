package com.ttknp.understandspringwebfluxandsecurity.controller;

import com.ttknp.understandspringwebfluxandsecurity.logging.Logback;
import com.ttknp.understandspringwebfluxandsecurity.model.Post;
import com.ttknp.understandspringwebfluxandsecurity.service.PostService;
import com.ttknp.responsecustomservice.constant.CommonStatus;
import com.ttknp.responsecustomservice.entity.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/posts")
public class PostController {

    private final PostService postService;
    private final Logback logback;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
        logback = new Logback(PostController.class);
    }

    @GetMapping(value = "/server")
    private ResponseEntity<ResponseObject<String>> server() {
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
    @GetMapping(value = {"/",""})
    private Mono<ResponseEntity<Flux<Post>>> reads() {
        return Mono.just(ResponseEntity
                .status((short) CommonStatus.OK[0])
                .body(postService.getPosts()));
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

    @GetMapping(value = "/search")
    private ResponseEntity<Mono<Post>> read(@RequestParam long id) {
        return ResponseEntity
                .status((short) CommonStatus.OK[0])
                .body(postService.getPost(id));
    }

    @PostMapping(value = "/save")
    private ResponseEntity<Mono<Post>> create(@RequestBody Mono<Post> postMono) {
        return ResponseEntity
                .status((short) CommonStatus.ACCEPTED[0])
                .body(postService.createPost(postMono));
    }

    @PutMapping(value = "/edit")
    private ResponseEntity<Mono<Boolean>> update(@RequestBody Mono<Post> postMono, @RequestParam long id) {
        return ResponseEntity
                .status((short) CommonStatus.ACCEPTED[0])
                .body(postService.updatePost(id,postMono));
    }

    @DeleteMapping(value = "/remove")
    private ResponseEntity<Mono<Boolean>> delete( @RequestParam long id) {
        return ResponseEntity
                .status((short) CommonStatus.ACCEPTED[0])
                .body(postService.deletePost(id));
    }

    @GetMapping("/protected")
    public Mono<ResponseEntity<String>> protectedEndpoint() {
        return Mono.just(ResponseEntity.ok("You have accessed a protected endpoint!"));
    }
}
