package com.rocketmotordesign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    private static Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Value("${cors.allowed.domains}")
    private String[] allowedCORSDomains;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/compute")
                .allowedOrigins(allowedCORSDomains);
    }
}