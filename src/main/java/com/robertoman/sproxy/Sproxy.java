package com.robertoman.sproxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Properties;

@EnableConfigurationProperties
@Slf4j
@SpringBootApplication
public class Sproxy {

    public static void main(String[] args) {
        SpringApplication sproxyApp = new SpringApplication(Sproxy.class);

        Properties properties = new Properties();
        properties.setProperty("spring.main.banner-mode", "off");
        properties.setProperty("spring.application.name", "sproxy");
        properties.setProperty("spring.output.ansi.enabled", "always");
        properties.setProperty("logging.level.org.springframework", "info");
        properties.setProperty("logging.file.name", "/var/log/sproxy/sproxy");

        sproxyApp.setDefaultProperties(properties);

        sproxyApp.run(args);
    }
}
