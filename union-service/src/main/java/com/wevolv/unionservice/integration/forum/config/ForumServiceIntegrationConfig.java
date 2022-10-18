package com.wevolv.unionservice.integration.forum.config;

import com.wevolv.unionservice.integration.forum.service.ForumService;
import com.wevolv.unionservice.integration.forum.service.impl.HttpForumServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ForumServiceIntegrationConfig {

 /*   @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
*/
    @Bean
    ForumService getForumService(RestTemplate getRestTemplate){
        return new HttpForumServiceImpl(getRestTemplate);
    }
}
