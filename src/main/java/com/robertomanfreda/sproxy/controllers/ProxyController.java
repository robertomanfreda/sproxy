package com.robertomanfreda.sproxy.controllers;

import com.robertomanfreda.sproxy.exceptions.ProxyException;
import com.robertomanfreda.sproxy.http.Extractor;
import com.robertomanfreda.sproxy.services.ProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
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

    /**
     * Request has body                 No
     * Successful response has body 	No
     * Safe 	                        Yes
     * Idempotent 	                    Yes
     * Cacheable 	                    Yes
     * Allowed in HTML forms 	        No
     *
     * @return {@link ResponseEntity}
     * @throws ProxyException // TODO ProxyException in HEAD
     * @throws IOException    // TODO IOException in HEAD
     */
    @RequestMapping(method = RequestMethod.HEAD)
    public ResponseEntity<?> head() throws ProxyException, IOException {
        HttpEntity<?> requestEntity = makeRequestEntity();
        HttpHead httpRequest = new HttpHead(Extractor.extractEntityUrl(httpServletRequest));
        return makeResponseEntity(proxyService.doProxy(requestEntity, httpRequest));
    }

    /**
     * Request has body                 No
     * Successful response has body 	Yes
     * Safe 	                        Yes
     * Idempotent 	                    Yes
     * Cacheable 	                    Yes
     * Allowed in HTML forms 	        No
     *
     * @return {@link ResponseEntity}
     * @throws ProxyException // TODO ProxyException in GET
     * @throws IOException    // TODO IOException in GET
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    public ResponseEntity<?> get() throws ProxyException, IOException {
        HttpEntity<?> requestEntity = makeRequestEntity();
        HttpGet httpRequest = new HttpGet(Extractor.extractEntityUrl(httpServletRequest));
        return makeResponseEntity(proxyService.doProxy(requestEntity, httpRequest));
    }

    /**
     * Request has body                 No
     * Successful response has body 	Yes
     * Safe 	                        No
     * Idempotent 	                    No
     * Cacheable 	                    Only if freshness information is included
     * Allowed in HTML forms 	        Yes
     *
     * @return {@link ResponseEntity}
     * @throws ProxyException // TODO ProxyException in POST
     * @throws IOException    // TODO IOException in POST
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE)
    // TODO POST
    public ResponseEntity<?> post() throws ProxyException, IOException {
        HttpEntity<?> requestEntity = makeRequestEntity();
        HttpPost httpRequest = new HttpPost(Extractor.extractEntityUrl(httpServletRequest));

        // TODO BODY?!

        return makeResponseEntity(proxyService.doProxy(requestEntity, httpRequest));
    }

    // String extractBodyFromBody() {
    //     String s = httpServletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    //     or log.info("Request has no body");

    private HttpEntity<?> makeRequestEntity() {
        HttpHeaders httpHeaders = Extractor.extractHttpHeaders(httpServletRequest);
        Map<String, String> urlParameters = Extractor.extractQueryParameters(httpServletRequest);
        return new HttpEntity<>(urlParameters, httpHeaders);
    }

    private ResponseEntity<?> makeResponseEntity(HttpResponse httpResponse) throws IOException {
        // Populating response body (some response has no response body so we return an empty string)
        String responseBody = null != httpResponse.getEntity() ? EntityUtils.toString(httpResponse.getEntity()) : "";

        // Populating response headers
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


        /*private <T extends HttpRequestBase> void setBody(T httpRequest,
                                                     HttpEntity<?> request) throws UnsupportedEncodingException {
        List<String> contentType = request.getHeaders().get("content-type");

        if (null != request.getBody() && null != contentType) {
            switch (contentType.get(0)) {
                case MediaType.APPLICATION_JSON_VALUE:
                    // TODO should manage with other types too
                    ((HttpPost) httpRequest).setEntity(new StringEntity(request.getBody().toString()));
                    break;
                case MediaType.APPLICATION_FORM_URLENCODED_VALUE:
                    break;
                default:
                    throw new UnsupportedEncodingException("Provided " + contentType.get(0) + " not supported");
            }
        }
    }*/

}
