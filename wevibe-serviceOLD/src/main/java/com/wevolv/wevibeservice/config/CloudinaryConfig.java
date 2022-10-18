package com.wevolv.wevibeservice.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("cloudinary")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudinaryConfig {
    private String cloudName;
    private String apiKey;
    private String apiSecret;
}
