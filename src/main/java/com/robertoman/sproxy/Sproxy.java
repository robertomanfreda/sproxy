package com.robertoman.sproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.Properties;

@EnableConfigurationProperties
@EnableDiscoveryClient
@SpringBootApplication
public class Sproxy {

    public static void main(String[] args) {
        SpringApplication sproxy = new SpringApplication(Sproxy.class);
        Properties properties = new Properties();
        properties.setProperty("spring.main.banner-mode", "off");
        properties.setProperty("spring.application.name", "sproxy");
        properties.setProperty("spring.output.ansi.enabled", "always");
        properties.setProperty("logging.level.org.springframework", "info");
        properties.setProperty("logging.file.name", "/var/log/sproxy/sproxy");
        sproxy.setDefaultProperties(properties);
        sproxy.run(args);
    }

}
