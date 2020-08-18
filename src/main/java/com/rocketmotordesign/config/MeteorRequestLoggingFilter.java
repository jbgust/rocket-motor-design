package com.rocketmotordesign.config;

import com.google.common.collect.Sets;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

public class MeteorRequestLoggingFilter extends CommonsRequestLoggingFilter {


    private static final String PREFIX_HTTP_STATUTS = "HTTP STATUT: ";

    private static final Set<String> ignoredURI = Sets.newHashSet("/actuator", "/auth");

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        logger.info(message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        logger.info(message);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(shouldLog(request)){
            logger.info(PREFIX_HTTP_STATUTS + response.getStatus());
        }
        super.doFilterInternal(request, response, filterChain);
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return !ignoredURI.stream()
                .map(ignoredUrl -> request.getRequestURI().contains(ignoredUrl))
                .reduce(Boolean::logicalOr)
                .orElse(false);
    }
}
