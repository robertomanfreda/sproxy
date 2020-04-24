package com.robertoman.sproxy.config;

import com.github.robertomanfreda.java.jwt.core.JavaJWT;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@ConditionalOnExpression("${config.security.enabled:true}")
@Configuration
public class JavaJWTConfig {

    @Setter
    @Value("${config.security.keystore.zip.path}")
    private String keystoreZipPath;

    @Bean
    public JavaJWT javaJWT() throws Exception {
        return new JavaJWT(Path.of(keystoreZipPath));
    }

}
