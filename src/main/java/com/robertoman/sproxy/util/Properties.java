package com.robertoman.sproxy.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;

import static com.robertoman.sproxy.util.ANSIColors.*;

public class Properties {

    public static java.util.Properties loadProperties() throws IOException {
        java.util.Properties properties = new java.util.Properties();
        String propertiesPath = System.getenv("sproxy.config.properties.path");
        String envProps = "sproxy.config.properties.path";

        if (null != propertiesPath) {
            System.out.println(ANSI_GREEN + "FOUND ENVIRONMENT VARIABLE " + ANSI_RESET +
                    ANSI_BLUE + envProps + ANSI_RESET +
                    " with value " + propertiesPath
            );

            if (propertiesPath.endsWith(".properties")) {
                properties.load(new FileInputStream(propertiesPath));
            } else if (propertiesPath.endsWith(".yml") || propertiesPath.endsWith(".yaml")) {
                properties = loadYamlNoVerbose(new FileSystemResource(propertiesPath));
            }

            System.out.println(ANSI_GREEN + "Successfully loaded external properties file." + ANSI_RESET);
        } else {
            String defaultProps = "default-properties.yaml";

            System.out.println(ANSI_RED + "CRITICAL WARNING" + ANSI_RESET + ": You should use an external properties " +
                    "file.\nSproxy is trying to load it reading the value of the environment variable named ["
                    + ANSI_BLUE + envProps + ANSI_RESET + "] but it can't be found.\nPlease consider adding that " +
                    "variable, for reference read the online documentation available on GitHub: " +
                    "https://github.com/robertomanfreda/sproxy/blob/master/README.md#modify-default-sproxy-properties" +
                    ANSI_YELLOW + "\nLoading " + defaultProps + "..." + ANSI_RESET);

            properties = loadYamlNoVerbose(new ClassPathResource(defaultProps));

            System.out.println(ANSI_GREEN + "Successfully loaded default properties file." + ANSI_RESET + " You are " +
                    "using a very little part of the Sproxy power!"
            );
        }

        return properties;
    }

    private static java.util.Properties loadYamlNoVerbose(Resource resource) {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(resource);

        ((Logger) LoggerFactory
                .getLogger("org.springframework.beans.factory.config.YamlPropertiesFactoryBean"))
                .setLevel(Level.INFO);

        return yamlPropertiesFactoryBean.getObject();
    }
}
