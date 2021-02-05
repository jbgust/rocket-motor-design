package com.rocketmotordesign.config;

import com.rocketmotordesign.security.jwt.AuthEntryPointJwt;
import com.rocketmotordesign.security.jwt.AuthTokenFilter;
import com.rocketmotordesign.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static java.util.Arrays.asList;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        // securedEnabled = true,
        // jsr250Enabled = true,
        prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${cors.allowed.domains}")
    private String[] allowedCORSDomains;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/auth/**", "/stripe/events").permitAll()
                .requestMatchers(EndpointRequest.to(
                        InfoEndpoint.class,
                        PrometheusScrapeEndpoint.class
                )).permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(asList(allowedCORSDomains));
        configuration.setAllowedMethods(asList("*"));
        configuration.setAllowedHeaders(asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/auth/signin", configuration);
        source.registerCorsConfiguration("/auth/signup", configuration);
        source.registerCorsConfiguration("/auth/reset-password", configuration);
        source.registerCorsConfiguration("/auth/reset-password/*", configuration);
        source.registerCorsConfiguration("/auth/validate/*", configuration);
        source.registerCorsConfiguration("/auth/resent-activation/*", configuration);

        source.registerCorsConfiguration("/compute", configuration);
        source.registerCorsConfiguration("/compute/cslot", configuration);
        source.registerCorsConfiguration("/compute/endburner", configuration);
        source.registerCorsConfiguration("/compute/finocyl", configuration);
        source.registerCorsConfiguration("/compute/moonburner", configuration);
        source.registerCorsConfiguration("/compute/rodtube", configuration);
        source.registerCorsConfiguration("/compute/star", configuration);
        source.registerCorsConfiguration("/export/rasp", configuration);

        source.registerCorsConfiguration("/propellants", configuration);
        source.registerCorsConfiguration("/propellants/*", configuration);
        source.registerCorsConfiguration("/motors", configuration);
        source.registerCorsConfiguration("/motors/*", configuration);
        return source;
    }
}
