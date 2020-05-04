package com.robertoman.sproxy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Order(0)
@Slf4j
public class SproxySecurityConfig {

    @Configuration
    @EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
    protected static class DefaultWebSecurityConfig {
    }

    /*@ConditionalOnExpression("${config.security.enabled:true}")
    @Configuration
    @EnableWebSecurity
    @Order(1)
    @Setter
    protected static class SproxyWebSecurityConfig extends WebSecurityConfigurerAdapter {

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

    }*/
}
