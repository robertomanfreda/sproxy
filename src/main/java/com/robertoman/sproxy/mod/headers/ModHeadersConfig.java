package com.robertoman.sproxy.mod.headers;

import lombok.Data;
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

    @Data
    @Slf4j
    public static class ModHeaders {

        private final Map<Pattern, Pair<String, String>> headersMap = new HashMap<>();

        private boolean allowOverrides;
        private Map<String, List<String>> map;

        public enum TypeHeader {
            REQUEST, RESPONSE
        }

        public void mod(String entityUrl, MultiValueMap<String, String> headers, TypeHeader typeHeader) {
            log.debug("Performing modifications on {} headers...", typeHeader);
            getHeadersMap().forEach((pattern, pair) -> {
                String headerKey = pair.getLeft();
                String headerValue = pair.getRight();

                if (pattern.matcher(entityUrl).find()) {
                    log.debug("[{}] matches the regex [{}]", entityUrl, pattern.pattern());
                    if (null == headers.get(headerKey)) {
                        headers.add(headerKey, headerValue);
                        log.debug("[{}]: [{}] - header successfully added to {} headers", headerKey, headerValue,
                                typeHeader
                        );
                    } else {
                        if (isAllowOverrides()) {
                            log.debug("Allow Overrides function enabled. Replacing old header: [{}]", headerKey);
                            headers.replace(headerKey, List.of(headerValue));
                            log.debug("[{}]: [{}] - successfully replaced header", headerKey, headerValue);
                        } else {
                            log.debug("Allow Overrides function is disabled... skipping header [{}]", headerKey);
                        }
                    }
                }
            });
        }

        @PostConstruct
        private void configureMap() {
            if (null != map && map.size() > 0) {
                map.forEach((regex, header) -> {
                    for (String h : header) {
                        if (!h.contains(":")) {
                            log.warn("Wrong header [{}] should be separated using [{}]", h, ":");
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
    }

    @Component
    @ConfigurationProperties(prefix = "config.mod.headers.request")
    @Slf4j
    public static class ModHeadersRequest extends ModHeaders {
    }

    @Component
    @ConfigurationProperties(prefix = "config.mod.headers.response")
    @Slf4j
    public static class ModHeadersResponse extends ModHeaders {
    }

}
