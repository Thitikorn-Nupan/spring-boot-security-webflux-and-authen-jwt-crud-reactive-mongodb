package com.ttknp.understandspringwebfluxandsecurity.configuration;

// *********

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.ttknp.understandspringwebfluxandsecurity.service.PostService;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static java.util.Collections.singletonList;

@Configuration
@EnableMongoRepositories(basePackageClasses = PostService.class)
@EnableConfigurationProperties
public class MongoDBConnectionConfig {

    // ** MongoProperties class is working for config mongodb you can use application.property instead this. (optional) ** In some cases, we need to register more than one bean of the same type.
    @Primary
    @Bean
    @ConfigurationProperties(prefix = "mongodb.ttknp") // specify prefix for mapping to property file
    public MongoProperties mongodbTTKNPProperties() {
        return new MongoProperties();
    }


    @Bean
    public MongoClient mongoClientConfig(
            // ** Why it knows it mapped correct bean  ** because i use @Primary
            MongoProperties mongoProperties) {
        MongoCredential credential = MongoCredential
                .createCredential(mongoProperties.getUsername(),
                        mongoProperties.getAuthenticationDatabase(), // ** importance
                        mongoProperties.getPassword());
        return MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder
                        .hosts(singletonList(new ServerAddress(mongoProperties.getHost(), mongoProperties.getPort()))))
                .credential(credential)
                .build());
    }

}