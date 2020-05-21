package com.robertoman.sproxy.feature.tls;

import com.robertoman.sproxy.util.Constants;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@ConditionalOnProperty(value = "config.feature.tls.enabled", havingValue = "true")
@Configuration
@ConfigurationProperties(prefix = "config.feature.tls")
@Setter
@Slf4j
public class FeatureTLSConfig {

    private ConfigurableEnvironment configurableEnvironment;

    private boolean enabled;
    private boolean httpToHttps;
    private String keyAlias;
    private String keyStorePassword;
    private String keyStore;
    private String keyStoreType;

    @PostConstruct
    private void aVoid() {
        log.debug("---------- FEATURE TLS ----------");
        log.debug("Feature TLS is enabled, configuring it...");
        MutablePropertySources propertySources = configurableEnvironment.getPropertySources();

        Map<String, Object> map = new HashMap<>();
        map.put("server.ssl.key-alias", keyAlias);
        map.put("server.ssl.key-store-password", keyStorePassword);
        map.put("server.ssl.key-store", keyStore);
        map.put("server.ssl.key-store-type", keyStoreType);

        if (enabled) {
            map.put("server.port", 8443);
        }

        propertySources.addFirst(new MapPropertySource("configurationProperties", map));

        log.debug("keystore file correctly loaded");
    }

    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
        return new TomcatServletWebServerFactory() {
            @PostConstruct
            private void addConnector() {
                if (httpToHttps) {
                    log.debug("http to https redirection is enabled, preparing http connector on port [{}]", 8080);

                    Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
                    connector.setScheme("http");
                    connector.setPort(8080);
                    connector.setSecure(false);
                    connector.setRedirectPort(8443);

                    addAdditionalTomcatConnectors(connector);

                    log.debug("HTTP requests will be redirected to HTTPs port 8443");
                }
            }

            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint(Constants.SECURITY_CONSTRAINT_CONFIDENTIAL);
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("");
                collection.addPattern("/");
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
    }

    @Autowired
    public void setConfigurableEnvironment(ConfigurableEnvironment configurableEnvironment) {
        this.configurableEnvironment = configurableEnvironment;
    }
}
