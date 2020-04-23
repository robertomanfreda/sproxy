package com.robertoman.sproxy.config;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import javax.annotation.PostConstruct;
import java.util.List;

@Profile("tunneling-proxy")
@Slf4j
public class TunnelingProxySecurityConfig {

    @Configuration
    @EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
    @Order(0)
    protected static class DefaultWebSecurityConfig {
    }

    @ConditionalOnExpression("${config.security.enabled:true}")
    @Configuration
    @EnableWebSecurity
    @Order(1)
    @Setter
    protected static class TunnelingProxyWebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Value("${config.security.username}")
        private String username;

        @Value("${config.security.password}")
        private String password;

        @Value("${config.security.methods}")
        private List<String> methods;

        @PostConstruct
        private void postConstruct() {
            // POST is required to permit login
            if (null != methods && !methods.contains("POST")) {
                methods.add("POST");
            }
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

            http.authorizeRequests()
                    .antMatchers("/login").authenticated()
                    .and().httpBasic()
                    .and().csrf().disable();

            http.authorizeRequests()
                    .antMatchers("/**").permitAll();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser(username)
                    .password("{noop}".concat(password))
                    .roles("USER");
        }

        @Override
        public void configure(WebSecurity web) {
            StrictHttpFirewall firewall = new StrictHttpFirewall();
            firewall.setAllowUrlEncodedDoubleSlash(true);
            firewall.setAllowedHttpMethods(methods);
            log.debug("Allowed HTTP methods from configuration are [{}]", methods.toArray());
            web.httpFirewall(firewall);
        }

    }

}
