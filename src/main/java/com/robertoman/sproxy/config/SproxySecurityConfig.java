package com.robertoman.sproxy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Order(0)
@Slf4j
public class SproxySecurityConfig {

    @Configuration
    @EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
    protected static class DefaultWebSecurityConfig {
    }
}
