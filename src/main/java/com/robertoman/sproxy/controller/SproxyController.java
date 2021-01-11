package com.robertoman.sproxy.controller;

import com.robertoman.sproxy.annotation.Delayed;
import com.robertoman.sproxy.annotation.Logging;
import com.robertoman.sproxy.annotation.ModUrl;
import com.robertoman.sproxy.exception.ProxyException;
import com.robertoman.sproxy.mod.headers.ModHeadersConfig.ModHeaders.TypeHeader;
import com.robertoman.sproxy.mod.headers.ModHeadersService;
import com.robertoman.sproxy.service.ProxyService;
import com.robertoman.sproxy.util.Extractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.robertoman.sproxy.util.Constants.INDEX_HTML;
import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
@Slf4j
public class SproxyController {

    private final ProxyService proxyService;
    private final HttpServletRequest httpServletRequest;
    private final ModHeadersService modHeadersService;

    @Value("${config.show-homepage}")
    private boolean showHomepage;

    public SproxyController(ProxyService proxyService, HttpServletRequest httpServletRequest,
                            @Nullable ModHeadersService modHeadersService) {
        this.proxyService = proxyService;
        this.httpServletRequest = httpServletRequest;
        this.modHeadersService = modHeadersService;
    }

    @GetMapping({"", "/"})
    public String index() {
        if (showHomepage) return INDEX_HTML;
        return null;
    }

    @GetMapping("/favicon.ico")
    public void noFavicon() {
    }

    @Logging
    @Delayed
    @ModUrl
    @RequestMapping(method = RequestMethod.DELETE, value = "/**", produces = MediaType.ALL_VALUE)
    public ResponseEntity<?> delete() throws ProxyException, IOException {
        HttpEntity<?> requestEntity = makeRequestEntity();
        HttpDelete httpRequest = new HttpDelete(Extractor.extractEntityUrl(httpServletRequest));
        return makeResponseEntity(proxyService.doProxy(requestEntity, httpRequest));
    }

    @Logging
    @Delayed
    @ModUrl
    @RequestMapping(method = RequestMethod.GET, value = "/**", produces = MediaType.ALL_VALUE)
    public ResponseEntity<?> get() throws ProxyException, IOException {
        HttpEntity<?> requestEntity = makeRequestEntity();
        HttpGet httpRequest = new HttpGet(Extractor.extractEntityUrl(httpServletRequest));
        return makeResponseEntity(proxyService.doProxy(requestEntity, httpRequest));
    }

    @Logging
    @Delayed
    @ModUrl
    @RequestMapping(method = RequestMethod.HEAD, value = "/**")
    public ResponseEntity<?> head() throws ProxyException, IOException {
        HttpEntity<?> requestEntity = makeRequestEntity();
        HttpHead httpRequest = new HttpHead(Extractor.extractEntityUrl(httpServletRequest));
        return makeResponseEntity(proxyService.doProxy(requestEntity, httpRequest));
    }

    @Logging
    @Delayed
    @ModUrl
    @RequestMapping(method = RequestMethod.OPTIONS, value = "/**")
    public ResponseEntity<?> options() throws ProxyException, IOException {
        HttpEntity<?> requestEntity = makeRequestEntity();
        HttpOptions httpRequest = new HttpOptions(Extractor.extractEntityUrl(httpServletRequest));
        return makeResponseEntity(proxyService.doProxy(requestEntity, httpRequest));
    }

    @Logging
    @Delayed
    @ModUrl
    @RequestMapping(method = RequestMethod.PATCH, value = "/**", consumes = MediaType.ALL_VALUE,
            produces = MediaType.ALL_VALUE
    )
    public ResponseEntity<?> patch() throws ProxyException, IOException, ServletException {
        HttpEntity<?> requestEntity = makeRequestEntity();
        HttpPatch httpRequest = new HttpPatch(Extractor.extractEntityUrl(httpServletRequest));
        setEntity(requestEntity, httpRequest);
        return makeResponseEntity(proxyService.doProxy(requestEntity, httpRequest));
    }

    @Logging
    @Delayed
    @ModUrl
    @RequestMapping(method = RequestMethod.POST, value = "/**", consumes = MediaType.ALL_VALUE,
            produces = MediaType.ALL_VALUE
    )
    public ResponseEntity<?> post() throws ProxyException, IOException, ServletException {
        HttpEntity<?> requestEntity = makeRequestEntity();
        HttpPost httpRequest = new HttpPost(Extractor.extractEntityUrl(httpServletRequest));
        setEntity(requestEntity, httpRequest);
        return makeResponseEntity(proxyService.doProxy(requestEntity, httpRequest));
    }

    @Logging
    @Delayed
    @ModUrl
    @RequestMapping(method = RequestMethod.PUT, value = "/**", consumes = MediaType.ALL_VALUE,
            produces = MediaType.ALL_VALUE
    )
    public ResponseEntity<?> put() throws ProxyException, IOException, ServletException {
        HttpEntity<?> requestEntity = makeRequestEntity();
        HttpPut httpRequest = new HttpPut(Extractor.extractEntityUrl(httpServletRequest));
        setEntity(requestEntity, httpRequest);
        return makeResponseEntity(proxyService.doProxy(requestEntity, httpRequest));
    }

    private HttpEntity<?> makeRequestEntity() {
        HttpHeaders httpHeaders = Extractor.extractHttpHeaders(httpServletRequest);

        // MOD HEADERS -> request headers
        if (null != modHeadersService) {
            modHeadersService.getModHeadersRequest().mod(Extractor.extractEntityUrl(httpServletRequest), httpHeaders, TypeHeader.REQUEST);
        }

        Map<String, String> urlParameters = Extractor.extractQueryParameters(httpServletRequest);
        return new HttpEntity<>(urlParameters, httpHeaders);
    }

    private <T extends HttpEntityEnclosingRequest> void setEntity(HttpEntity<?> requestEntity, T httpRequest)
            throws IOException, ServletException {

        String type = httpServletRequest.getContentType();
        if (type.contains("application/x-www-form-urlencoded")) {
            List<NameValuePair> parameters = Extractor.extractFormParameters(httpServletRequest);
            if (parameters.size() > 0) {
                httpRequest.setEntity(new UrlEncodedFormEntity(parameters));
            }
        } else if (type.contains("multipart/form-data")) {
            Collection<Part> parts = httpServletRequest.getParts();
            if (parts.size() > 0) {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                MediaType contentType = requestEntity.getHeaders().getContentType();
                if (null != contentType) {
                    builder.setBoundary(contentType.getParameter("boundary"));
                }

                for (Part part : parts) {
                    if (null != part.getContentType()) {
                        builder.addBinaryBody(
                                part.getName(), part.getInputStream().readAllBytes(),
                                ContentType.MULTIPART_FORM_DATA, part.getSubmittedFileName()
                        );
                    } else {
                        builder.addTextBody(part.getName(), new String(part.getInputStream().readAllBytes()));
                    }
                }

                httpRequest.setEntity(builder.build());
            }
        } else {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(httpServletRequest.getInputStream(), UTF_8)
            );

            String body = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            if (body.length() > 0) {
                httpRequest.setEntity(new StringEntity(body));
            }
        }
    }

    private ResponseEntity<?> makeResponseEntity(HttpResponse httpResponse) throws IOException {
        // Populating response headers
        MultiValueMap<String, String> responseHeaders = new HttpHeaders();
        Stream.of(httpResponse.getAllHeaders()).forEach(header ->
                responseHeaders.add(header.getName(), header.getValue())
        );

        // MOD HEADERS -> response headers
        if (null != modHeadersService) {
            modHeadersService.getModHeadersResponse().mod(
                    Extractor.extractEntityUrl(httpServletRequest), responseHeaders, TypeHeader.RESPONSE
            );
        }

        // Populating response body (some response has no response body so we return an empty string)
        if (null != httpResponse.getEntity() && null != httpResponse.getEntity().getContent()) {
            return new ResponseEntity<>(
                    new InputStreamResource(httpResponse.getEntity().getContent()),
                    responseHeaders,
                    Objects.requireNonNull(HttpStatus.resolve(httpResponse.getStatusLine().getStatusCode()))
            );
        } else {
            return new ResponseEntity<>(
                    "",
                    responseHeaders,
                    Objects.requireNonNull(HttpStatus.resolve(httpResponse.getStatusLine().getStatusCode()))
            );
        }
    }

}
