package com.rocketmotordesign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static java.util.Arrays.asList;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${cors.allowed.domains}")
    private String[] allowedCORSDomains;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers("/compute", "/export/rasp")
                .permitAll()
//                .requestMatchers(EndpointRequest.to(PrometheusScrapeEndpoint.class))
//                .authenticated()
//                .and()
//                .formLogin()
                .and()
                .csrf().disable()
        ;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(asList(allowedCORSDomains));
        configuration.setAllowedMethods(asList("*"));
        configuration.setAllowedHeaders(asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/compute", configuration);
        source.registerCorsConfiguration("/export/rasp", configuration);
        return source;
    }
}
