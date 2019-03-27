package com.rocketmotordesign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Value("#{'${cors.allowed.domains}'.split(',')}")
    private List<String> allowedCORSDomains;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/compute")
                .allowedOrigins(allowedCORSDomains.toArray(new String[]{}));
    }
}