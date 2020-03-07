package com.robertomanfreda.sproxy.controllers;

import com.robertomanfreda.sproxy.http.Extractor;
import com.robertomanfreda.sproxy.services.ProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.util.EntityUtils;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;


@Slf4j
@RestController
@RequestMapping("/**")
@RequiredArgsConstructor
public class ProxyController {

    private final ProxyService proxyService;
    private final HttpServletRequest httpServletRequest;


    // Request has body                 No
    // Successful response has body 	No
    // Safe 	                        Yes
    // Idempotent 	                    Yes
    // Cacheable 	                    Yes
    // Allowed in HTML forms 	        No
    @RequestMapping(method = RequestMethod.HEAD)
    public ResponseEntity<?> head() throws Exception {
        HttpEntity<?> requestEntity = makeRequestEntity();
        HttpHead httpRequest = new HttpHead(Extractor.extractEntityUrl(httpServletRequest));
        return makeResponseEntity(proxyService.doProxy(requestEntity, httpRequest));
    }

    // Request has body                 No
    // Successful response has body 	Yes
    // Safe 	                        Yes
    // Idempotent 	                    Yes
    // Cacheable 	                    Yes
    // Allowed in HTML forms 	        No
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    public ResponseEntity<?> get() throws Exception {
        HttpEntity<?> requestEntity = makeRequestEntity();
        HttpGet httpRequest = new HttpGet(Extractor.extractEntityUrl(httpServletRequest));
        return makeResponseEntity(proxyService.doProxy(requestEntity, httpRequest));
    }

    // Request has body                 No
    // Successful response has body 	Yes
    // Safe 	                        No
    // Idempotent 	                    No
    // Cacheable 	                    Only if freshness information is included
    // Allowed in HTML forms 	        Yes
    /*@RequestMapping(method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE)
    public ResponseEntity<?> post(HttpServletRequest httpServletRequest) throws Exception {
        return null;
    }*/

    // String extractBodyFromBody() {
    //     String s = httpServletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    //     or log.info("Request has no body");

    private HttpEntity<?> makeRequestEntity() {
        HttpHeaders httpHeaders = Extractor.extractHttpHeaders(httpServletRequest);
        Map<String, String> urlParameters = Extractor.extractUrlParameters(httpServletRequest);
        return new HttpEntity<>(urlParameters, httpHeaders);
    }

    private ResponseEntity<?> makeResponseEntity(HttpResponse httpResponse) throws IOException {
        // Some response has no response body so we return an emty string
        String responseBody = null != httpResponse.getEntity() ? EntityUtils.toString(httpResponse.getEntity()) : "";

        MultiValueMap<String, String> responseHeaders = new HttpHeaders();
        Stream.of(httpResponse.getAllHeaders()).forEach(header ->
                responseHeaders.add(header.getName(), header.getValue())
        );

        return new ResponseEntity<>(
                responseBody,
                responseHeaders,
                Objects.requireNonNull(HttpStatus.resolve(httpResponse.getStatusLine().getStatusCode()))
        );
    }

}
