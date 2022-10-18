package com.wevolv.payment.mongo.config;

import java.util.List;

import com.wevolv.payment.mongo.OffsetDateTimeReadConverter;
import com.wevolv.payment.mongo.OffsetDateTimeWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(
                List.of(new OffsetDateTimeReadConverter(),
                        new OffsetDateTimeWriteConverter()));
    }
}