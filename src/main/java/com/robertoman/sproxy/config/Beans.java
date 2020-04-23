package com.robertoman.sproxy.config;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {

    @Bean
    public CloseableHttpClient httpClient() {
        return HttpClients.custom()
                .setDefaultRequestConfig(
                        RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()
                )
                .build();
    }

}
