package com.ttknp.understandspringwebfluxandsecurity.configuration;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;


/**
    Spring Data MongoDB adds _class in the mongo documents to handle polymorphic behavior of java inheritance.
    If you want to remove _class just drop following Config class in your code.
    It has many ways to remove _class (when create it is alive)  and this way it easy for me
    this MongoDBConfig config class we use to Remove "_class" attribute from MongoDB
*/
@Configuration
public class MongoDBDocumentConfig implements InitializingBean  {
    private MappingMongoConverter mappingMongoConverter; // for ignoring "_class" property

    @Autowired
    public MongoDBDocumentConfig(MappingMongoConverter mappingMongoConverter) {
        this.mappingMongoConverter = mappingMongoConverter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
    }

}