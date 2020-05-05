package com.robertoman.sproxy.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Extractor {

    public static String extractEntityUrl(HttpServletRequest httpServletRequest) {
        String queryString = httpServletRequest.getQueryString();

        StringBuilder urlBuilder = new StringBuilder(
                httpServletRequest.getRequestURI().replaceFirst("/", "")
        );

        if (null != queryString) {
            urlBuilder.append("?");
            urlBuilder.append(queryString);
        }

        return urlBuilder.toString();
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

    public static List<NameValuePair> extractFormParameters(HttpServletRequest request) {
        List<NameValuePair> parameters = new ArrayList<>();

        Stream.of(request.getParameterMap()).forEach(stringMap -> stringMap.forEach((key, values) -> {
                    Stream.of(values).forEach(value -> {
                        String queryString = Optional.ofNullable(request.getQueryString()).orElse("");
                        if (!queryString.contains(key + "=" + value)) {
                            parameters.add(new BasicNameValuePair(key, value));
                        }
                    });
                }
        ));

        return parameters;
    }

    public static Map<String, String> extractQueryParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();

        Stream.of(request.getParameterMap()).forEach(
                stringMap -> stringMap.forEach((key, values) -> {
                            Stream.of(values).forEach(value -> {
                                String queryString = request.getQueryString();
                                if (null != queryString && queryString.contains(key + "=" + value)) {
                                    parameters.put(key, value);
                                }
                            });
                        }
                ));

        return parameters;
    }
}
