package com.robertoman.sproxy.controllers;

import com.robertoman.sproxy.response.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ConditionalOnExpression("${config.security.enabled:true}")
@RequestMapping("/login")
@RestController
@Slf4j
public class SecurityController {

    @PostMapping
    public LoginResponse login() {
        // TODO session && bearer generation
        return LoginResponse.builder().bearer("1234").build();
    }

}
