package com.robertoman.sproxy.controllers;

import com.github.robertomanfreda.java.jwt.core.JavaJWT;
import com.robertoman.sproxy.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@ConditionalOnExpression("${config.security.enabled:true}")
@RequestMapping("/login")
@RequiredArgsConstructor
@RestController
@Slf4j
public class SecurityController {

    private final JavaJWT javaJWT;

    @PostMapping
    public LoginResponse login() throws Exception {
        // TODO session && bearer generation
        return LoginResponse.builder()
                .bearer(javaJWT.generate("issuer", "audience", Map.of("key", "value"), 1000))
                .build();
    }

}
