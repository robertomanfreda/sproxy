package com.robertoman.sproxy.mod.headers;

import com.robertoman.sproxy.mod.headers.ModHeadersConfig.ModHeadersRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.robertoman.sproxy.mod.headers.ModHeadersConfig.ModHeadersResponse;

@ConditionalOnProperty(value = "config.mod.headers.enabled", havingValue = "true")
@Getter
@RequiredArgsConstructor
@Service
@Slf4j
public class ModHeadersService {

    private final ModHeadersRequest modHeadersRequest;
    private final ModHeadersResponse modHeadersResponse;

    @PostConstruct
    private void logConfig() {
        log.debug("---------- MOD HEADERS ----------");
        log.debug("Configuring Mod headers request...");
        log.debug("modHeadersRequest -> {}", modHeadersRequest.toString());
        log.debug("Completed Mod headers request configuration.");

        log.debug("Configuring Mod headers response...");
        log.debug("modHeadersResponse -> {}", modHeadersResponse.toString());
        log.debug("Completed Mod headers response configuration.");
    }
}
