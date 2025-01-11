package com.ttknp.understandspringwebfluxandsecurity;

import com.ttknp.understandspringwebfluxandsecurity.configuration.security.SecurityWebConfig;
import com.ttknp.understandspringwebfluxandsecurity.model.Post;
import com.ttknp.understandspringwebfluxandsecurity.repository.PostRepository;
import com.ttknpdev.entity.ResponseObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

// ** remember we're mocking so we don't access database we access only layer (service) logic
// ** response follow your api
// ** Note jwt doesn't matter for testing
@ExtendWith(SpringExtension.class) // We are using @ExtendWith( SpringExtension.class ) to support testing in Junit 5. In Junit 4
@SpringBootTest
@Import(SecurityWebConfig.class)
public class WebFluxClientTests {

    private WebTestClient webTestClient;

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private PostRepository postRepository;


    @BeforeEach
    public void setUp() throws Exception {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    public void testHttpGetServer() {
        webTestClient.get()
                .uri("/api/server")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseObject.class)
                .value(responseObject -> responseObject.getData().equals("erver is running"));
    }

    @Test
    @WithMockUser(username="user",password = "",roles={"USER"}) // username , password it's not necessary but roles is
    public void testHttpGetReadsPosts() {
        // Flux<Post> employeeList = postRepository.findAll();
        webTestClient.get()
                .uri("/api/posts")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Post.class)
                .hasSize(3);
    }

    @Test
    @WithMockUser(username="admin",password = "12345",roles={"USER"})
    public void testHttpGetReadPost() {
        webTestClient.get()
                .uri(String.format("/api/post?id=%s", 1))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(1L);
    }

    @Test
    @WithMockUser(username="admin",password = "12345",roles={"ADMIN"})
    public void testHttpPostCreatePost() {
        webTestClient.post()
                .uri("/api/post")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(new Post(1L,"","")))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("topic").isEqualTo("");
    }

    @Test
    @WithMockUser(username="admin",password = "12345",roles={"ADMIN"})
    public void testHttpPutUpdatePost() {
        webTestClient.put()
                .uri(String.format("/api/post?id=%s", 1))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(new Post(1L,"","")))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(Boolean.class)
                .isEqualTo(true);
    }


    @Test
    @WithMockUser(username="admin",password = "12345",roles={"ADMIN"})
    public void testHttpDeleteDeletePost() {
        webTestClient.delete()
                .uri(String.format("/api/post?id=%s", 1))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(Boolean.class)
                .isEqualTo(true);
    }


}
