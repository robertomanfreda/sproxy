package com.robertoman.sproxy.mod.headers;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
@ConditionalOnProperty(value = "config.mod.headers.enabled", havingValue = "true")
public class ModHeadersConfig {

    @Getter
    @Setter
    @Slf4j
    public abstract static class ModHeaders {

        private final Map<Pattern, Pair<String, String>> headersMap = new HashMap<>();

        private boolean allowOverrides;
        private Map<String, List<String>> map;

        @PostConstruct
        private void configureMap() {
            if (null != map && map.size() > 0) {
                map.forEach((regex, header) -> {
                    for (String h : header) {
                        if (!h.contains(":")) {
                            log.warn("Wrong header [{}] should be separated using [{}[", h, ":");
                            log.warn("Skipping header [{}]", h);
                            continue;
                        }

                        int index = h.indexOf(":");
                        String left = h.substring(0, index).trim();
                        String right = h.substring(index + 1).trim();

                        headersMap.put(Pattern.compile(regex), Pair.of(left, right));
                    }
                });
            }
        }

        @Override
        public String toString() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }

        public abstract void addHeaders(String entityUrl, MultiValueMap<String, String> headers);
    }

    @Component
    @ConfigurationProperties(prefix = "config.mod.headers.request")
    @Slf4j
    public static class ModHeadersRequest extends ModHeaders {

        @Override
        public void addHeaders(String entityUrl, MultiValueMap<String, String> headers) {

        }
    }

    @Component
    @ConfigurationProperties(prefix = "config.mod.headers.response")
    @Slf4j
    public static class ModHeadersResponse extends ModHeaders {

        @Override
        public void addHeaders(String entityUrl, MultiValueMap<String, String> responseHeaders) {
            log.debug("Adding response headers...");
            getHeadersMap().forEach((pattern, pair) -> {
                String headerKey = pair.getLeft();
                String headerValue = pair.getRight();

                if (pattern.matcher(entityUrl).find()) {
                    log.debug("[{}] matches the regex [{}]", entityUrl, pattern.pattern());
                    if (null == responseHeaders.get(headerKey)) {
                        responseHeaders.add(headerKey, headerValue);
                        log.debug("[{}]: [{}] - header successfully added to response headers", headerKey, headerValue);
                    } else {
                        if (isAllowOverrides()) {
                            log.debug("Allow Overrides function enabled. Replacing old header: [{}]", headerKey);
                            responseHeaders.replace(headerKey, List.of(headerValue));
                            log.debug("[{}]: [{}] - successfully replaced header", headerKey, headerValue);
                        } else {
                            log.debug("Allow Overrides function is disabled... skipping header [{}]", headerKey);
                        }
                    }
                }
            });
        }
    }

}
