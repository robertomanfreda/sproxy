package com.robertoman.sproxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@EnableConfigurationProperties
@Slf4j
@SpringBootApplication
public class Sproxy {

    public static void main(String[] args) throws IOException {
        SpringApplication application = new SpringApplication(Sproxy.class);

        Properties properties = loadProperties();
        /*https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.xhtml?&page=99*/
        properties.setProperty("server.port", "6380");
        properties.setProperty("spring.application.name", "sproxy");
        properties.setProperty("spring.output.ansi.enabled", "ALWAYS");
        properties.setProperty("spring.banner.image.invert", "false");
        application.setDefaultProperties(properties);

        application.run(args);
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        String propertiesPath = System.getenv("sproxy.config.properties.path");
        String envProps = "sproxy.config.properties.path";

        if (null != propertiesPath) {
            log.debug("FOUND ENVIRONMENT variable [{}] with value [{}] - Loading properties...", envProps,
                    propertiesPath
            );

            if (propertiesPath.endsWith(".properties")) {
                properties.load(new FileInputStream(propertiesPath));
            } else if (propertiesPath.endsWith(".yml")) {
                YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
                yamlPropertiesFactoryBean.setResources(new FileSystemResource(propertiesPath));
                properties = yamlPropertiesFactoryBean.getObject();
            }

            log.debug("Successfully loaded Properties.");
        } else {
            String defaultProps = "default-properties.yml";

            log.warn("You should use an externalized properties file.\nSproxy is trying to load it reading the value" +
                    " of the environment variable named [{}] but it can't be found. Please consider adding that " +
                    "variable, for reference read the online documentation available on GitHub.\n" +
                    "Loading [{}]...", envProps, defaultProps);

            properties.load(new ClassPathResource(defaultProps).getInputStream());
        }

        return properties;
    }

}
