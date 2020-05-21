package com.robertoman.sproxy.mod.waf;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.List;

@ConditionalOnProperty(value = "config.mod.waf.enabled", havingValue = "true")
@ConfigurationProperties(prefix = "config.mod.waf.allows")
@Configuration
@EnableAutoConfiguration(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableWebSecurity
//@Order(0) is for src/main/java/com/robertoman/sproxy/config/SproxySecurityConfig.java
@Order(1)
@Setter
@Slf4j
public class ModWafConfig extends WebSecurityConfigurerAdapter {

    private boolean backSlash;
    private boolean urlEncodedDoubleSlash;
    private boolean urlEncodedPercent;
    private boolean urlEncodedPeriod;
    private boolean urlEncodedSlash;
    private boolean semicolon;
    private List<String> httpMethods;
    private List<String> hostNames;

    @Override
    public void configure(WebSecurity web) {
        log.debug("---------- MOD WAF ----------");
        log.debug("Configuring mod WAF...");
        StrictHttpFirewall firewall = new StrictHttpFirewall();

        log.debug("Allowing backSlash: [{}]", backSlash);
        firewall.setAllowBackSlash(backSlash);

        log.debug("Allowing urlEncodedDoubleSlash: [{}]", urlEncodedDoubleSlash);
        firewall.setAllowUrlEncodedDoubleSlash(urlEncodedDoubleSlash);

        log.debug("Allowing urlEncodedPercent: [{}]", urlEncodedPercent);
        firewall.setAllowUrlEncodedPercent(urlEncodedPercent);

        log.debug("Allowing urlEncodedPeriod: [{}]", urlEncodedPeriod);
        firewall.setAllowUrlEncodedPeriod(urlEncodedPeriod);

        log.debug("Allowing urlEncodedSlash [{}]", urlEncodedSlash);
        firewall.setAllowUrlEncodedSlash(urlEncodedSlash);

        log.debug("Allowing semicolon [{}]", semicolon);
        firewall.setAllowSemicolon(semicolon);

        if (null != hostNames && hostNames.size() > 0) {
            firewall.setAllowedHostnames((h) -> hostNames.stream().anyMatch(hn -> hn.equals(h)));
        }

        firewall.setAllowedHttpMethods(httpMethods);
        log.debug("Allowed HTTP methods from configuration are [{}]", httpMethods.toString());

        web.httpFirewall(firewall);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().anyRequest().permitAll().and().csrf().disable();
    }

}
