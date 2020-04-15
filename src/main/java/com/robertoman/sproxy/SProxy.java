package com.robertoman.sproxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@Slf4j
@SpringBootApplication
public class SProxy {

    public static void main(String[] args) {
        SpringApplication.run(SProxy.class, args);
    }

}
