package com.robertoman.sproxy.config;

import com.github.robertomanfreda.java.jwt.JavaJWT;
import org.springframework.context.annotation.Bean;

import java.net.URL;

//@Configuration
public class JavaJWTConfig {

    @Bean
    public JavaJWT javaJWT() throws Exception {
        return new JavaJWT(new URL("http://localhost/keystore.zip"));
    }
}
