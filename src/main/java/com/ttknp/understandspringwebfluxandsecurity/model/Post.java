package com.ttknp.understandspringwebfluxandsecurity.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "posts")
@Getter
@Setter
public class Post {
    @Id
    private Long id;
    private String topic;
    private String details;
    private Date datetime;

    public Post(Long id, String topic, String details) {
        this.id = id;
        this.topic = topic;
        this.details = details;
        this.datetime = new Date();
    }


}
