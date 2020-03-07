package com.robertomanfreda.sproxy.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Extractor {

    public static String extractEntityUrl(HttpServletRequest httpServletRequest) {
        final StringBuilder url = new StringBuilder(httpServletRequest.getRequestURI().replaceFirst("/", ""));
        if (httpServletRequest.getParameterMap().size() > 0) {
            url.append("?");
            Stream.of(httpServletRequest.getParameterMap()).forEach(
                    parameters -> parameters.forEach(
                            (key, value) -> url.append(key).append("=").append(value[0]).append("&")
                    )
            );
            url.deleteCharAt(url.length() - 1);
        }
        return url.toString();
    }

    public static HttpHeaders extractHttpHeaders(HttpServletRequest request) {
        return Collections
                .list(request.getHeaderNames())
                .stream()
                .collect(
                        Collectors.toMap(
                                Function.identity(),
                                header -> Collections.list(request.getHeaders(header)),
                                (oldValue, newValue) -> newValue,
                                HttpHeaders::new
                        )
                );
    }

    public static Map<String, String> extractUrlParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();

        Stream.of(request.getParameterMap()).forEach(stringMap -> stringMap.forEach((k, v) ->
                Stream.of(v).forEach(value -> parameters.put(k, value))
        ));

        return parameters;
    }

}
