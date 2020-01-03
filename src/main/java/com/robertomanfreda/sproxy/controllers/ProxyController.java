package com.robertomanfreda.sproxy.controllers;

import com.robertomanfreda.sproxy.services.ProxyService;
import com.robertomanfreda.sproxy.utils.UrlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> delete(RequestEntity<?> request) {    // TODO delete
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/**")
    public ResponseEntity<?> get(RequestEntity<?> request) throws Exception {
        String url = UrlUtils.removeSecWafUrlFromUrl(request);
        return getSuccessResponse(proxyService.doProxy(request, url, new HttpGet(url)));
    }

    @RequestMapping(method = RequestMethod.HEAD, value = "/**", produces = MediaType.ALL_VALUE)
    public ResponseEntity<?> head(RequestEntity<?> request) throws Exception {
        String url = UrlUtils.removeSecWafUrlFromUrl(request);
        return getSuccessResponse(proxyService.doProxy(request, url, new HttpHead(url)));
    }

    @RequestMapping(method = RequestMethod.OPTIONS, value = "/**")
    public ResponseEntity<?> options(RequestEntity<?> request) throws Exception {
        String url = UrlUtils.removeSecWafUrlFromUrl(request);
        return getSuccessResponse(proxyService.doProxy(request, url, new HttpOptions(url)));
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/**")
    public ResponseEntity<?> patch(RequestEntity<?> request) { // TODO patch
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/**",
            consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE)
    public ResponseEntity<?> post(HttpServletRequest servletRequest) throws Exception {
        // TODO Refactor this
        HttpEntity<?> request = getHttpEntity(servletRequest);
        final StringBuilder url = new StringBuilder(servletRequest.getRequestURI().replaceFirst("/", ""));
        if (servletRequest.getParameterMap().size() > 0) {
            url.append("?");
            Stream.of(servletRequest.getParameterMap()).forEach((stringMap -> stringMap.forEach((key, value) -> url.append(key).append("=").append(value[0]).append("&"))));
            url.deleteCharAt(url.length() - 1);
        }
        HttpPost httpPost = new HttpPost(url.toString());
        return getSuccessResponse(proxyService.doProxy(request, url.toString(), httpPost));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/**")
    public ResponseEntity<?> put(RequestEntity<?> request) {   // TODO put
        return null;
    }

    @RequestMapping(method = RequestMethod.TRACE, value = "/**")
    public ResponseEntity<?> trace(RequestEntity<?> request) { // TODO trace
        return null;
    }

    private HttpEntity<?> getHttpEntity(HttpServletRequest servletRequest) throws IOException {
        HttpEntity<?> request;

        HttpHeaders httpHeaders = (Collections
                .list(servletRequest.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        h -> Collections.list(servletRequest.getHeaders(h)),
                        (oldValue, newValue) -> newValue,
                        HttpHeaders::new
                ))
        );

        String body = servletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        if (!body.isEmpty()) {
            request = new HttpEntity<>(body, httpHeaders);
        } else {
            Map<String, String> payload = new HashMap<>();
            Stream.of(servletRequest.getParameterMap()).forEach(stringMap -> stringMap.forEach((k, v) ->
                    Stream.of(v).forEach(value -> payload.put(k, value))
            ));
            request = new HttpEntity<>(payload, httpHeaders);
        }

        return request;
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

    // TODO remember to remove this test endpoint
    @GetMapping("/test")
    public String test(@RequestParam String name, @RequestHeader String head) {
        return "hello " + name + " ----- HEAD -_> " + head;
    }

}
