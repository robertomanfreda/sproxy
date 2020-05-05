package com.robertoman.sproxy.service;

import com.robertoman.sproxy.exception.ProxyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProxyService {

    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private final HttpClient httpClient;

    public <T extends HttpRequestBase> HttpResponse doProxy(HttpEntity<?> request, T httpRequest)
            throws ProxyException {

        if (!request.getHeaders().isEmpty()) {
            setHeaders(httpRequest, request);
        }

        String url = httpRequest.getURI().toString();
        if (url.contains(HTTPS) || url.contains(HTTP)) {
            return directProxy(httpRequest);
        } else {
            return automaticProxy(httpRequest, url);
        }
    }

    private <T extends HttpRequestBase> HttpResponse directProxy(T httpRequest) throws ProxyException {
        HttpResponse httpResponse;

        try {
            httpResponse = httpClient.execute(httpRequest);
        } catch (IOException e) {
            log.error("directProxy failed - using {}", HTTPS);
            throw new ProxyException("Direct proxy ERROR.");
        }

        return httpResponse;
    }

    private <T extends HttpRequestBase> HttpResponse automaticProxy(T httpRequest, String url) throws ProxyException {
        HttpResponse httpResponse;

        // Try using HTTPS
        try {
            httpResponse = sendRequestWithCustomProtocol(httpRequest, HTTPS, url);
        } catch (IOException e) {
            log.error("automaticProxy failed - using {} - calling url {}", HTTPS, url);

            // Try using HTTP
            try {
                httpResponse = sendRequestWithCustomProtocol(httpRequest, HTTP, url);
            } catch (IOException ioe) {
                log.error("automaticProxy failed - using {} - calling url {}", HTTP, url);
                throw new ProxyException("Automatic proxy ERROR.");
            }
        }

        return httpResponse;
    }

    private <T extends HttpRequestBase> void setHeaders(T httpRequest, HttpEntity<?> request) {
        request.getHeaders().forEach((key, value) -> {
            // Without ignoring these headers the request will fail!
            if (!key.equalsIgnoreCase("host") &&
                    !key.equalsIgnoreCase("content-length")) {
                httpRequest.addHeader(key, value.get(0));
            }
        });
    }

    private <T extends HttpRequestBase> HttpResponse sendRequestWithCustomProtocol(T httpRequest, String protocol,
                                                                                   String url) throws IOException {
        httpRequest.setURI(URI.create(protocol + "://" + url));
        return httpClient.execute(httpRequest);
    }

}
