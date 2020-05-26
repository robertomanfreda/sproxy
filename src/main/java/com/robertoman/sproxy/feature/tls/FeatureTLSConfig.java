package com.robertoman.sproxy.feature.tls;

import com.robertoman.sproxy.util.Constants;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@ConditionalOnProperty(value = "config.feature.tls.enabled", havingValue = "true")
@Configuration
@ConfigurationProperties(prefix = "config.feature.tls")
@Setter
@Slf4j
public class FeatureTLSConfig {
    private boolean httpToHttps;
    private String keyAlias;
    private String keyStorePassword;
    private String keyStore;
    private String keyStoreType;

    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                if (httpToHttps) {
                    tomcat.getConnector().setRedirectPort(8443);
                }
                return super.getTomcatWebServer(tomcat);
            }

            @PostConstruct
            private void addConnector() {
                log.debug("---------- FEATURE TLS ----------");
                log.debug("Feature TLS is enabled, configuring it...");

                Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
                connector.setScheme("https");
                connector.setPort(8443);
                connector.setSecure(true);

                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
                protocol.setSSLEnabled(true);
                protocol.setKeyAlias(keyAlias);
                protocol.setKeystoreFile(keyStore);
                protocol.setKeystorePass(keyStorePassword);
                protocol.setKeystoreType(keyStoreType);

                addAdditionalTomcatConnectors(connector);
                log.debug("keystore file correctly loaded");
            }

            @Override
            protected void postProcessContext(Context context) {
                if (httpToHttps) {
                    SecurityConstraint securityConstraint = new SecurityConstraint();
                    securityConstraint.setUserConstraint(Constants.SECURITY_CONSTRAINT_CONFIDENTIAL);
                    SecurityCollection collection = new SecurityCollection();
                    collection.addPattern("");
                    collection.addPattern("/");
                    collection.addPattern("/*");
                    securityConstraint.addCollection(collection);
                    context.addConstraint(securityConstraint);
                }
            }
        };
    }
}
