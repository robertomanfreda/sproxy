package com.robertoman.sproxy.services;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static com.robertoman.sproxy.utils.Constants.*;

@Service
@Slf4j
@ConfigurationProperties(prefix = "config.cors")
@RequiredArgsConstructor
@Setter
public class CorsService {

    private final Map<Pattern, String> corsMap = new HashMap<>();

    private boolean enabled;
    private List<String> rules;

    @PostConstruct
    public void doConfig() {
        if (!enabled) return;

        log.debug("STARTED CORS configuration...");

        rules.forEach(r -> {
            int index = r.indexOf(REG_KEY_CLOSE) + REG_KEY_CLOSE.length();

            String regex = r.substring(0, index)
                    .replace(REG_KEY_START, "")
                    .replace(REG_KEY_CLOSE, "");

            String value = r.substring(index)
                    .replace(REG_VALUE_START, "")
                    .replace(REG_VALUE_CLOSE, "");

            corsMap.put(Pattern.compile(regex), value);
        });

        log.debug("Added the following CORS rules:");
        AtomicInteger rulesCounter = new AtomicInteger(1);

        corsMap.forEach((rule, corsValue) -> {
            log.debug("Rule {}: Regex [{}], CORS Value [{}]", rulesCounter.get(), rule.pattern(), corsValue);
            rulesCounter.getAndIncrement();
        });

        log.debug("COMPLETED CORS configuration.");
    }

    public void addCorsHeader(String entityUrl, MultiValueMap<String, String> responseHeaders) {
        if (!enabled) return;

        // Modifying CORS in response headers
        corsMap.forEach((pattern, corsValue) -> {
            if (pattern.matcher(entityUrl).find()) {
                if (null != responseHeaders.get(CORS_HEADER)) {
                    log.debug("Adding CORS header [{}: {}]", CORS_HEADER, corsValue);
                    responseHeaders.replace(CORS_HEADER, List.of(corsValue));
                } else {
                    responseHeaders.add(CORS_HEADER, corsValue);
                }
            }
        });
    }

}
