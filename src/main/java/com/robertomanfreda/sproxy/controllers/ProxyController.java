package com.robertomanfreda.sproxy.controllers;

import com.robertomanfreda.sproxy.services.ProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class ProxyController {

    private final ProxyService proxyService;

    @RequestMapping(method = RequestMethod.DELETE, value = "/**")
    public ResponseEntity<?> delete(HttpServletRequest httpServletRequest) {    // TODO delete
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/**")
    public ResponseEntity<?> get(HttpServletRequest httpServletRequest) throws Exception {
        HttpEntity<?> request = getHttpEntity(httpServletRequest);
        HttpGet httpGet = new HttpGet(getEntityUrl(httpServletRequest));
        return getSuccessResponse(proxyService.doProxy(request, httpGet));
    }

    @RequestMapping(method = RequestMethod.HEAD, value = "/**", produces = MediaType.ALL_VALUE)
    public ResponseEntity<?> head(HttpServletRequest httpServletRequest) {
        return null;
    }

    @RequestMapping(method = RequestMethod.OPTIONS, value = "/**")
    public ResponseEntity<?> options(HttpServletRequest httpServletRequest) {
        return null;
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/**")
    public ResponseEntity<?> patch(HttpServletRequest httpServletRequest) { // TODO patch
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/**",
            consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE)
    public ResponseEntity<?> post(HttpServletRequest httpServletRequest) throws Exception {
        HttpEntity<?> request = getHttpEntity(httpServletRequest);
        HttpPost httpPost = new HttpPost(getEntityUrl(httpServletRequest));
        return getSuccessResponse(proxyService.doProxy(request, httpPost));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/**")
    public ResponseEntity<?> put(HttpServletRequest httpServletRequest) {   // TODO put
        return null;
    }

    @RequestMapping(method = RequestMethod.TRACE, value = "/**")
    public ResponseEntity<?> trace(HttpServletRequest httpServletRequest) { // TODO trace
        return null;
    }

    private HttpEntity<?> getHttpEntity(HttpServletRequest httpServletRequest) throws IOException {
        HttpEntity<?> request;

        HttpHeaders httpHeaders = (Collections
                .list(httpServletRequest.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        h -> Collections.list(httpServletRequest.getHeaders(h)),
                        (oldValue, newValue) -> newValue,
                        HttpHeaders::new
                ))
        );

        String body = httpServletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        if (!body.isEmpty()) {
            request = new HttpEntity<>(body, httpHeaders);
        } else {
            Map<String, String> payload = new HashMap<>();
            Stream.of(httpServletRequest.getParameterMap()).forEach(stringMap -> stringMap.forEach((k, v) ->
                    Stream.of(v).forEach(value -> payload.put(k, value))
            ));
            request = new HttpEntity<>(payload, httpHeaders);
        }

        return request;
    }

    private String getEntityUrl(HttpServletRequest httpServletRequest) {
        final StringBuilder url = new StringBuilder(httpServletRequest.getRequestURI()
                .replaceFirst("/", ""));

        if (httpServletRequest.getParameterMap().size() > 0) {
            url.append("?");
            Stream.of(httpServletRequest.getParameterMap())
                    .forEach(stringMap -> stringMap
                            .forEach((key, value) -> url.append(key).append("=").append(value[0]).append("&")));
            url.deleteCharAt(url.length() - 1);
        }

        return url.toString();
    }

    private ResponseEntity<?> getSuccessResponse(HttpResponse httpResponse) throws IOException {
        String entity = "";
        if (null != httpResponse.getEntity()) {
            entity = EntityUtils.toString(httpResponse.getEntity());
        }

        MultiValueMap<String, String> responseHeaders = new HttpHeaders();
        Stream.of(httpResponse.getAllHeaders()).forEach(header ->
                responseHeaders.add(header.getName(), header.getValue())
        );

        return new ResponseEntity<>(
                entity,
                responseHeaders,
                Objects.requireNonNull(HttpStatus.resolve(httpResponse.getStatusLine().getStatusCode()))
        );
    }

}
