//package com.wevolv.unionservice.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.servlet.view.InternalResourceViewResolver;
//
///**
// *
// * @author kundankumar
// */
//@Configuration
//public class CorsConfiguration {
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("https://api.wevolv.net","https://www.api.wevolv.net")
//                        .allowedHeaders("*").
//                        allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
//                        .maxAge(3600);
//            }
//
//            @Override
//            public void configureViewResolvers(ViewResolverRegistry registry) {
//                InternalResourceViewResolver resolver = new InternalResourceViewResolver();
//                registry.viewResolver(resolver);
//            }
//
//        };
//    }
//}
